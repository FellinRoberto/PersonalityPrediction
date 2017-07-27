import pandas as pd
from sklearn.metrics import mean_squared_error

predictions=pd.read_csv('../bow/bowPrediction.csv', engine='c', header=None)
true_values = pd.read_csv('../dataset/y_test.csv', engine='c')

print len(predictions)
print len(true_values)

print mean_squared_error(true_values.ix[:,0], predictions.ix[:,0])
