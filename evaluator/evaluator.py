import pandas as pd
from sklearn.metrics import mean_squared_error
import os
import numpy as np

mean_predictions=pd.read_csv('../meanPrediction/y_pred.csv', engine='c')
bow_predictions=pd.read_csv('../bow/bowPrediction.csv', engine='c',header=None)
true_values = pd.read_csv('../dataset/y_test.csv', engine='c')
true_valuesNew = pd.read_csv('../minimalpipeline-master/TreeKernel/y_testNew.csv', engine='c',header=None)

os.system("../svm/src/svm_learn -t 5 -C T -z r  ../minimalpipeline-master/TreeKernel/train1.dat model1")
os.system("../svm/src/svm_classify  ../minimalpipeline-master/TreeKernel/test1.dat model1 output1.txt")

os.system("../svm/src/svm_learn -t 5 -C T -z r  ../minimalpipeline-master/TreeKernel/train2.dat model2")
os.system("../svm/src/svm_classify  ../minimalpipeline-master/TreeKernel/test2.dat model2 output2.txt")

os.system("../svm/src/svm_learn -t 5 -C T -z r  ../minimalpipeline-master/TreeKernel/train3.dat model3")
os.system("../svm/src/svm_classify  ../minimalpipeline-master/TreeKernel/test3.dat model3 output3.txt")

os.system("../svm/src/svm_learn -t 5 -C T -z r  ../minimalpipeline-master/TreeKernel/train4.dat model4")
os.system("../svm/src/svm_classify  ../minimalpipeline-master/TreeKernel/test4.dat model4 output4.txt")

os.system("../svm/src/svm_learn -t 5 -C T -z r  ../minimalpipeline-master/TreeKernel/train5.dat model5")
os.system("../svm/src/svm_classify  ../minimalpipeline-master/TreeKernel/test5.dat model5 output5.txt")


#treeKernel_predictions=[]
with open('output1.txt') as f:
    tKp1 = f.read().splitlines()

with open('output2.txt') as f:
    tKp2 = f.read().splitlines()

with open('output3.txt') as f:
    tKp3 = f.read().splitlines()

with open('output4.txt') as f:
    tKp4 = f.read().splitlines()

with open('output5.txt') as f:
    tKp5 = f.read().splitlines()


tKp1=map(float, tKp1)
tKp2=map(float, tKp2)
tKp3=map(float, tKp3)
tKp4=map(float, tKp4)
tKp5=map(float, tKp5)

tKp1 = np.array([tKp1])
tKp1=tKp1.T

tKp2 = np.array([tKp2])
tKp2=tKp2.T

tKp3 = np.array([tKp3])
tKp3=tKp3.T

tKp4 = np.array([tKp4])
tKp4=tKp4.T

tKp5 = np.array([tKp5])
tKp5=tKp5.T

print tKp1

tK=np.append(tKp1, tKp2, axis=1)
tK=np.append(tK, tKp3, axis=1)
tK=np.append(tK, tKp4, axis=1)
tK=np.append(tK, tKp5, axis=1)


print "mean error:", mean_squared_error(true_values, mean_predictions)

print "bow error:", mean_squared_error(true_values, bow_predictions)

print "tree Kernel error:", mean_squared_error(true_valuesNew, tK)