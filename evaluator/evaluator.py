import pandas as pd
from sklearn.metrics import mean_squared_error

mean_predictions=pd.read_csv('../meanPrediction/y_pred.csv', engine='c')
bow_predictions=pd.read_csv('../bow/bowPrediction.csv', engine='c',header=None)
true_values = pd.read_csv('../dataset/y_test.csv', engine='c')

print "mean error:", mean_squared_error(true_values, mean_predictions)

print "bow error:", mean_squared_error(true_values, bow_predictions)
