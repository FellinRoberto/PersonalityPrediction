import pandas as pd
from sklearn.metrics import mean_squared_error
import os
import numpy as np

#n1=2000 n2=500
# F3 t6: 94.80, 88.40, 85.40, 91.40, 53.4   numeri intorno a 1 (quinta numeri piu bassi)
# F3 t5 c5: uguale a sopra
# F3 t5 c1: uguale a sopra
#F3 t5 c10: 94.80, 88.40, 85.40, 91.40, 56.8
#F3 t5 c20: 94.80, 88.40, 85.40, 91.40, 56.2

#
#n1=15000
#n2=5000
#-t 5 -c 10 -C T -F 1
# 95.62 88.12 86.62 90.62 54.98
# 95.62 88.12 86.62 90.62 55.06

n1=5000
n2=5000

log=""
for c in range(1,6):
    log=log+"MODEL"+ str(c) +"\n \n"
    os.system("head -" + str(n1) + " train" + str(
        c) + "C.dat > train" + str(c) + "b.dat")
    os.system("head -" + str(n2) + " test" + str(
        c) + "C.dat > test" + str(c) + "b.dat")

    f=os.popen("../../svm/src/svm_learn -t 5 -c 10 -C T -F 1 train" + str(c) + "b.dat model"+str(c))
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