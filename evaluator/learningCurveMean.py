import pandas as pd
import numpy as np
from sklearn.metrics import mean_squared_error
import matplotlib.pyplot as plt

matrix = pd.read_csv('./../minimalpipeline-master/TreeKernel/y_trainNew.csv', engine='c',header=None)
true_values = pd.read_csv('./../minimalpipeline-master/TreeKernel/y_testNew.csv', engine='c',header=None)

x= matrix.mean()
x=pd.DataFrame(x)
x=x.transpose()


#print true_values



n_trains =[50, 100, 500, 1000, 2000]

plt.title('Learning Curve of Mean')
plt.legend(loc='upper right')
plt.ylabel('mean square error')
plt.xlabel('number of posts for training')

result=[]
color=['ro-','bo-','go-','co-','yo-']
for j in range (0,5):

    result = []
    for i in range(0,len(n_trains)):
        y=x
        y = pd.concat([y] * n_trains[i])
        result.append(mean_squared_error(true_values[true_values.columns[j]].head(n_trains[i]), y[y.columns[j]]))

    plt.plot(n_trains, result, color[j], label="personality"+str(j))




plt.show()