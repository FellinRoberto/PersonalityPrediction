import pandas as pd
from sklearn.metrics import mean_squared_error
import os
import numpy as np

true_values = pd.read_csv('../../dataset/y_test.csv', engine='c')
true_valuesNew = pd.read_csv('../../minimalpipeline-master/TreeKernel/y_testNew.csv', engine='c',header=None)

# -v 3
#-F proviamo 0 1 3

n1=10
n2=100

log=""
for c in range(1,6):
    log=log+"MODEL"+ str(c) +"\n \n"
    #os.system("head -" + str(n1) + " train" + str(c) + ".dat > ../minimalpipeline-master/TreeKernel/train" + str(c) + "b.dat")
    os.system("head -" + str(n2) + " test" + str(c) + ".dat > test" + str(c) + "b.dat")


    #head -10 test1.dat > test1b.dat
    f=os.popen("../../svm/src/svm_learn -t 0 -z r train" + str(c) + ".dat model"+str(c))
    f=f.read()
    log = log + f + "\n"
    print f
    f=os.popen("../../svm/src/svm_classify  test" + str(c) + "b.dat train"+str(c)+".dat output"+str(c)+".txt")
    f = f.read()
    log = log + f + "\n"
    print f




#os.system("../svm/src/svm_learn -t 5 -C T  ../minimalpipeline-master/TreeKernel/train2.dat model2")
#os.system("../svm/src/svm_classify  ../minimalpipeline-master/TreeKernel/test2.dat model2 output2.txt")
# e in train del svm learn cambiamo e mettiamo 0 se <2.5 e 1 se >2.5


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


#print "mean error:", mean_squared_error(true_values, mean_predictions)

#print "bow error:", mean_squared_error(true_valuesNew.head(n2), bow_predictions)

print "tree Kernel error:", mean_squared_error(true_valuesNew.head(n2), tK)

log = log + "bow error:"+ str(mean_squared_error(true_valuesNew.head(n2), bow_predictions)) + "\n"

log = log + "tree Kernel error:" + str(mean_squared_error(true_valuesNew.head(n2), tK)) + "\n"


out_file = open("log.txt","w")
out_file.write(log)
out_file.close()

