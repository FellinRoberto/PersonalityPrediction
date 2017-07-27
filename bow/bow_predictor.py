import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
import csv
import numpy as np
import xgboost as xgb

x_train = pd.read_csv('../dataset/X_train.csv', engine='c', encoding='latin-1')
x_test = pd.read_csv('../dataset/X_test.csv', engine='c', encoding='latin-1')
y_train = pd.read_csv('../dataset/y_train.csv', engine='c', encoding='latin-1')

n_features = 5000  # numero massimo di parole prese in considerazione in ogni post

tfidf_vectorizer = TfidfVectorizer(max_df=0.95, min_df=0.01, max_features=n_features, stop_words='english')

print x_train
bow_train = tfidf_vectorizer.fit_transform(x_train['status_update'])

bow_train = bow_train.todense()

np.savetxt('tfidfTrain.csv', bow_train, delimiter=',')

bow_test = tfidf_vectorizer.transform(x_test['status_update'])
bow_test = bow_test.todense()
np.savetxt('tfidfTest.csv', bow_test, delimiter=',')
# tfidf_df = pd.DataFrame(tfidf.toarray())
# tfidf_df.to_csv('tfidf.csv', index=False, quoting=csv.QUOTE_NONNUMERIC, doublequote=True)

param_dist = {'objective': 'reg:linear', 'n_estimators': 2}

clf = xgb.XGBRegressor(**param_dist)

y_train = y_train.as_matrix()
y_train = y_train[:, 0]

clf.fit(bow_train, y_train, eval_metric='logloss', verbose=True)

prediction = clf.predict(bow_test)
print prediction
print prediction.shape

np.savetxt('bowPrediction.csv', prediction, delimiter=',')
