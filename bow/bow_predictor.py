import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
import csv
import numpy as np
import xgboost as xgb

#x_train = pd.read_csv('../dataset/X_train.csv', engine='c', encoding='latin-1')
#x_test = pd.read_csv('../dataset/X_test.csv', engine='c', encoding='latin-1')
#y_train = pd.read_csv('../dataset/y_train.csv', engine='c', encoding='latin-1')

n1=20000
n2=10000

x_train = pd.read_csv('../minimalpipeline-master/TreeKernel/x_trainNew.csv', engine='c', encoding='latin-1',header=None)
x_test = pd.read_csv('../minimalpipeline-master/TreeKernel/x_testNew.csv', engine='c', encoding='latin-1',header=None)
y_train = pd.read_csv('../minimalpipeline-master/TreeKernel/y_trainNew.csv', engine='c', encoding='latin-1',header=None)

x_train=x_train.head(n1)
y_train=y_train.head(n1)
x_test=x_test.head(n2)

n_features = 5000  # numero massimo di parole prese in considerazione in ogni post

tfidf_vectorizer = TfidfVectorizer(max_df=0.95, min_df=0.01, max_features=n_features, stop_words='english')

#print x_train
#bow_train = tfidf_vectorizer.fit_transform(x_train['status_update'])
bow_train = tfidf_vectorizer.fit_transform(x_train[0])

bow_train = bow_train.todense()

np.savetxt('tfidfTrain.csv', bow_train, delimiter=',')

#bow_test = tfidf_vectorizer.transform(x_test['status_update'])
bow_test = tfidf_vectorizer.transform(x_test[0])

bow_test = bow_test.todense()
np.savetxt('tfidfTest.csv', bow_test, delimiter=',')

# tfidf_df = pd.DataFrame(tfidf.toarray())
# tfidf_df.to_csv('tfidf.csv', index=False, quoting=csv.QUOTE_NONNUMERIC, doublequote=True)

y_train = y_train.as_matrix()

n=5

prediction = np.empty(shape=[len(x_test), 0])


for x in range(0, n):
    #clf = xgb.XGBRegressor()
#    clf=xgb.XGBRegressor(learning_rate = 0.1, n_estimators=1000,
#                           max_depth=5, min_child_weight=1,
#                           gamma=0, subsample=0.8,
#                           colsample_bytree=0.8, objective= "reg:linear",
#                           nthread=-1,scale_pos_weight=1, seed=27)


    clf = xgb.XGBRegressor(learning_rate = 0.2, max_depth = 3)
    y_t = y_train[:, x]
    clf.fit(bow_train, y_t)
    temp =  clf.predict(bow_test)
    prediction = np.column_stack((prediction, temp))
    print x

np.savetxt('bowPrediction.csv', prediction, delimiter=',')
print "bowPrediction.csv gererated"