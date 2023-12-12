package com.example.sapr.service;

import com.example.sapr.payload.Bar;
import com.example.sapr.payload.Constructor;
import com.example.sapr.payload.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.sapr.service.MainService.showErrorDialog;
import static org.apache.commons.math3.linear.MatrixUtils.createRealMatrix;

public enum Processor {
    INSTANCE;
    private String NX_FORMATTER = "N%dx: (%f * x) + (%f)";
    private String SIGMA_FORMATTER = "σ%dx: (%f * x) + (%f)";
    private String UX_FORMATTER = "U%dx: (%f * x^2) + (%f * x) + (%f)";
    public final List<NxCalculate> nxTotal = new ArrayList<>();
    public final List<SigmaCalculate> sigmaTotal = new ArrayList<>();
    public final List<UXCalculate> uxTotal = new ArrayList<>();

    void process(Constructor construction) {
        String processedValue = calculate(construction);
        Stage stage = new Stage();
        stage.setResizable(false);
        TextArea textArea = new TextArea(processedValue);
        Scene scene = new Scene(textArea, 400.0, 400.0);
        stage.setScene(scene);
        stage.show();
    }

    private String calculate(Constructor construction) {
        int nodeCount = construction.getNodes().size();
        int barCount = nodeCount - 1;
        List<Double> elasticMods = construction.getBars().stream().map(Bar::getElasticMod).collect(Collectors.toList());
        List<Double> areas = construction.getBars().stream().map(Bar::getArea).collect(Collectors.toList());
        List<Double> lengths = construction.getBars().stream().map(Bar::getLength).collect(Collectors.toList());
        double[] nodeLoads = construction.getNodes().stream().mapToDouble(Node::getFxLoad).toArray();
        double[] barLoads = construction.getBars().stream().mapToDouble(Bar::getQxLoad).toArray();
        double[][] reactionVectorData = new double[nodeCount][1];
        double[][] reactionMatrixData = new double[nodeCount][nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (i == j && i > 0 && i < barCount) {
                    reactionMatrixData[i][j] = elasticMods.get(i - 1) * areas.get(i - 1) / lengths.get(i - 1) + elasticMods.get(j) * areas.get(j) / lengths.get(j);
                } else if (i == j + 1) {
                    reactionMatrixData[i][j] = -(elasticMods.get(j) * areas.get(j)) / lengths.get(j);
                } else if (j == i + 1) {
                    reactionMatrixData[i][j] = -(elasticMods.get(i) * areas.get(i)) / lengths.get(i);
                } else {
                    reactionMatrixData[i][j] = 0.0;
                }
            }
        }

        for (int idx = 1; idx < barCount; idx++) {
            reactionVectorData[idx][0] = nodeLoads[idx] + barLoads[idx] * lengths.get(idx) / 2 + barLoads[idx - 1] * lengths.get(idx - 1) / 2;
        }

        if (construction.getLeftSupport()) {
            reactionMatrixData[0][0] = 1.0;
            reactionMatrixData[0][1] = 0.0;
            reactionMatrixData[1][0] = 0.0;
            reactionVectorData[0][0] = 0.0;
        } else {
            reactionMatrixData[0][0] = elasticMods.get(0) * areas.get(0) / lengths.get(0);
            reactionVectorData[0][0] = nodeLoads[0] + barLoads[0] * lengths.get(0) / 2;
        }

        if (construction.getRightSupport()) {
            reactionMatrixData[barCount][barCount] = 1.0;
            reactionMatrixData[barCount - 1][barCount] = 0.0;
            reactionMatrixData[barCount][barCount - 1] = 0.0;
            reactionVectorData[barCount][0] = 0.0;
        } else {
            reactionMatrixData[barCount][barCount] = elasticMods.get(barCount - 1) * areas.get(barCount - 1) / lengths.get(barCount - 1);
            reactionVectorData[barCount][0] = nodeLoads[barCount] + barLoads[barCount - 1] * lengths.get(barCount - 1) / 2;
        }

        double[] uZeros = new double[barCount];
        double[] uLengths = new double[barCount];
        RealMatrix deltaVector = createDeltaMatrix(reactionMatrixData, reactionVectorData);

        for (int idx = 0; idx < barCount; idx++) {
            uZeros[idx] = deltaVector.getEntry(idx, 0);
        }
        System.arraycopy(uZeros, 1, uLengths, 0, barCount - 1);
        uLengths[barCount - 1] = deltaVector.getEntry(barCount, 0);

        StringBuilder builder = new StringBuilder();
        for (int idx = 0; idx < barCount; idx++) {
            double elasticity = elasticMods.get(idx);
            double area = areas.get(idx);
            double length = lengths.get(idx);
            double nxb = calculateNxb(elasticity, area, length, uZeros[idx], uLengths[idx], barLoads[idx]);
            double uxa = calculateUxa(elasticity, area, barLoads[idx]);
            double uxb = calculateUxb(elasticity, area, length, uZeros[idx], uLengths[idx], barLoads[idx]);
            builder.append(String.format(SIGMA_FORMATTER, idx + 1, -barLoads[idx] / areas.get(idx), nxb / areas.get(idx))).append("\n");
            builder.append(String.format(NX_FORMATTER, idx + 1, -barLoads[idx], nxb)).append("\n");
            builder.append(String.format(UX_FORMATTER, idx + 1, uxa, uxb, uZeros[idx])).append("\n");
            uxTotal.add(new UXCalculate(uxa, uxb));
            sigmaTotal.add(new SigmaCalculate(-barLoads[idx] / areas.get(idx), nxb / areas.get(idx)));
            nxTotal.add(new NxCalculate(-barLoads[idx], nxb));

        }
        return builder.toString();

    }

    public Results calculate(Constructor constructor, double x) {
        if (x < 0) {
            showErrorDialog("Параметры конструкции заданы неверно");
            return null;
        }
        double test = 0;
        for (int i = 0; i <= constructor.getBars().size(); i++) {
            test += constructor.getBars().get(i).getLength();
            if (x <= test)
                return new Results(x, sigmaTotal.get(i).calculate(x), nxTotal.get(i).calculate(x), uxTotal.get(i).calculate(x));
        }
        return null;
    }

    public List<Results> calculate(Constructor constructor, Integer ind, double step, int xPrecision) {
        List<Results> results = new ArrayList<>();
        for (double i = 0; Precision.round(i, xPrecision) <= constructor.getBars().get(ind).getLength(); i += step) {
            results.add(new Results(Precision.round(i, xPrecision), sigmaTotal.get(ind).calculate(i), nxTotal.get(ind).calculate(i), uxTotal.get(ind).calculate(i)));
        }
        return results;
    }

    private RealMatrix createDeltaMatrix(double[][] reactionMatrixData, double[][] reactionVectorData) {
        RealMatrix reactionMatrix = createRealMatrix(reactionMatrixData);
        RealMatrix reactionVector = createRealMatrix(reactionVectorData);
        RealMatrix inverseReactionMatrix = new LUDecomposition(reactionMatrix).getSolver().getInverse();
        return inverseReactionMatrix.multiply(reactionVector);
    }

    private Double calculateNxb(Double elasticMod, Double area, Double length, Double Up0, Double UpL, Double q) {
        return elasticMod * area / length * (UpL - Up0) + q * length / 2;
    }

    private Double calculateUxb(Double E, Double A, Double L, Double Up0, Double UpL, Double q) {
        return (UpL - Up0 + q * L * L / (2 * E * A)) / L;
    }

    private Double calculateUxa(Double E, Double A, Double q) {
        return -q / (2 * E * A);
    }
}
