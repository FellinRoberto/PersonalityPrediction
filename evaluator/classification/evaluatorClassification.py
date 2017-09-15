import pandas as pd
from sklearn.metrics import mean_squared_error
import os
import numpy as np

# -v 3
#-F proviamo 0 1 3

n1=500
n2=2000

log=""
for c in range(1,6):
    log=log+"MODEL"+ str(c) +"\n \n"
    os.system("head -" + str(n1) + " train" + str(
        c) + "C.dat > train" + str(c) + "b.dat")
    os.system("head -" + str(n2) + " test" + str(
        c) + "C.dat > test" + str(c) + "b.dat")

    f=os.popen("../../svm/src/svm_learn -t 5 -C T train" + str(c) + "b.dat model"+str(c))
    f=f.read()
    log = log + f + "\n"
    print f
    f=os.popen("../../svm/src/svm_classify  test" + str(c) + "b.dat model"+str(c)+" output"+str(c)+".txt")
    f = f.read()
    log = log + f + "\n"
    print f




#os.system("../svm/src/svm_learn -t 5 -C T  ../minimalpipeline-master/TreeKernel/train2.dat model2")
#os.system("../svm/src/svm_classify  ../minimalpipeline-master/TreeKernel/test2.dat model2 output2.txt")
# e in train del svm learn cambiamo e mettiamo 0 se <2.5 e 1 se >2.5


out_file = open("log.txt","w")
out_file.write(log)
out_file.close()