package com.syshuman.kadir.haircolor3.model;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import com.syshuman.kadir.haircolor3.eventbus.MessageEvents;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.ml.SVM;

import java.io.IOException;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class MySVM {
    svm _svm;
    RestServer restServer;
    Context context;
    double[][] xtrain;
    double[][] ytrain;
    double[][] xtest;
    double[][] ytest;

    public MySVM(Context context) {
        this.context = context;
    }

    public svm_model svmTrain(double[][] xtrain, double[][] ytrain) {
        svm_problem prob = new svm_problem();

        int recordCount = xtrain.length;
        int featureCount = xtrain[0].length;
        xtrain = scale(xtrain);
        prob.y = new double[recordCount];
        prob.l = recordCount;
        prob.x = new svm_node[recordCount][featureCount];
        for (int i = 0; i < recordCount; i++){
            double[] features = xtrain[i];
            prob.x[i] = new svm_node[features.length];
            for (int j = 0; j < features.length; j++){
                svm_node node = new svm_node();
                node.index = j;
                node.value = features[j];
                prob.x[i][j] = node;
            }
            prob.y[i] = ytrain[i][0];
        }

        svm_parameter param = new svm_parameter();
        param.probability = 1;
        param.gamma = 0.5;
        param.nu = 0.5;
        param.C = 32768;
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.cache_size = 20000;
        param.eps = 0.001;
        svm_model model = svm.svm_train(prob, param);

        String path = context.getFilesDir().getPath();

        try {
            svm.svm_save_model(path+"/svm_model.svm", model);
            Toast.makeText(context, "Model File is saved at " + path, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Unable to saved at " + path, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return model;
    }

    private double[][] scale(double[][] xtrain) {
        int recordCount = xtrain.length;
        int featureCount = xtrain[0].length;
        for(int i=0;i<recordCount; i++)
            for(int j=0;j<featureCount; j++)
                xtrain[i][j] = scale(xtrain[i][j]);
        return xtrain;
    }

    private double scale(double value) {
        int xmin = 200;
        int xmax = 400;
        int y_min = 0;
        int y_max = 1;
        return y_min  + (y_max-y_min) * (value-xmin) / (xmax-xmin);
    }


    public double[] predict(double[][] xdata) {
        xdata = scale(xdata);
        String path = context.getFilesDir().getPath() + "/svm_model.svm" ;
        try {
            svm_model model = svm.svm_load_model(path);
            return svmPredict(xdata, model);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private double[] svmPredict(double[][] xtest, svm_model model) {
        double[] yPred = new double[xtest.length];
        for(int k = 0; k < xtest.length; k++){
            double[] fVector = xtest[k];
            svm_node[] nodes = new svm_node[fVector.length];
            for (int i = 0; i < fVector.length; i++)  {
                svm_node node = new svm_node();
                node.index = i;
                node.value = fVector[i];
                nodes[i] = node;
            }

            int totalClasses = model.nr_class;
            int[] labels = new int[totalClasses];
            svm.svm_get_labels(model,labels);
            double[] prob_estimates = new double[totalClasses];
            yPred[k] = svm.svm_predict_probability(model, nodes, prob_estimates);
        }
        return yPred;
    }

    public void getTrainData() {
        //restServer.getTrainData("data.txt");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrainData(MessageEvents.onTrainedData event) {
        //Toast.makeText(context, event.data.length, Toast.LENGTH_LONG).show();
    }

    public void cvSVM() {
        float[] labelArray = new float[20];
        int sizeOfDataSet = 12;
        Mat responses = new Mat(1, sizeOfDataSet, CvType.CV_32F);
        responses.put(0,0,labelArray);

        SVM svm = SVM.create();

    }
}
