package com.syshuman.kadir.haircolor3.model;

import android.content.Context;
import android.widget.Toast;
import com.syshuman.kadir.haircolor3.eventbus.MessageEvents;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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

    public MySVM(Context context, RestServer restServer, svm _svm) {
        this.context = context;
        this._svm = _svm;
        this.restServer = restServer;
        svm_model model = svmTrain(xtrain, ytrain);
        double[] yprediction = svmPredict(xtest, model);
    }

    private svm_model svmTrain(double[][] xtrain, double[][] ytrain) {
        svm_problem prob = new svm_problem();
        int recordCount = xtrain.length;
        int featureCount = xtrain[0].length;
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
        param.C = 100;
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.cache_size = 20000;
        param.eps = 0.001;
        svm_model model = svm.svm_train(prob, param);
        return model;
    }

    public double[] svmPredict(double[][] xtest, svm_model model) {
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

            int totalClasses = 2;
            int[] labels = new int[totalClasses];
            svm.svm_get_labels(model,labels);
            double[] prob_estimates = new double[totalClasses];
            yPred[k] = svm.svm_predict_probability(model, nodes, prob_estimates);
        }
        return yPred;
    }

    public void getTrainData() {
        restServer.getTrainData("data.txt");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrainData(MessageEvents.onTrainData event) {
        Toast.makeText(context, event.data.length, Toast.LENGTH_LONG).show();
    }
}
