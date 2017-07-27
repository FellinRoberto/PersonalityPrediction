import pandas as pd
from sklearn.metrics import mean_squared_error

mean_predictions=pd.read_csv('../meanPrediction/y_pred.csv', engine='c')
bow_predictions=pd.read_csv('../bow/bowPrediction.csv', engine='c', header=None)
true_values = pd.read_csv('../dataset/y_test.csv', engine='c')

personality_index = 0

print "mean error:", mean_squared_error(true_values.ix[:,personality_index], mean_predictions.ix[:,personality_index])
print "bow error:", mean_squared_error(true_values.ix[:,personality_index], bow_predictions.ix[:,personality_index])
