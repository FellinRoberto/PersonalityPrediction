import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.datasets import dump_svmlight_file
import csv
import numpy as np
import xgboost as xgb

#x_train = pd.read_csv('../dataset/X_train.csv', engine='c', encoding='latin-1')
#x_test = pd.read_csv('../dataset/X_test.csv', engine='c', encoding='latin-1')
#y_train = pd.read_csv('../dataset/y_train.csv', engine='c', encoding='latin-1')

n1=10
n2=10000

x_train = pd.read_csv('../../minimalpipeline-master/TreeKernel/x_trainNew.csv', engine='c', encoding='latin-1',header=None)
x_test = pd.read_csv('../../minimalpipeline-master/TreeKernel/x_testNew.csv', engine='c', encoding='latin-1',header=None)
y_train = pd.read_csv('../../minimalpipeline-master/TreeKernel/y_trainNew.csv', engine='c', encoding='latin-1',header=None)

x_train=x_train.head(n1)
y_train=y_train.head(n1)
x_test=x_test.head(n2)
print(x_train)
print(y_train)

n_features = 5000  # numero massimo di parole prese in considerazione in ogni post

tfidf_vectorizer = TfidfVectorizer(max_df=0.95, min_df=0.01, max_features=n_features, stop_words='english')

#print x_train
#bow_train = tfidf_vectorizer.fit_transform(x_train['status_update'])
bow_train = tfidf_vectorizer.fit_transform(x_train[0])

bow_train = bow_train.todense()

np.savetxt('tfidfTrain.csv', bow_train, delimiter=',')

y_train = y_train.as_matrix()

print(bow_train)
print(y_train[:, 0])
for c in range(0,5):
    dump_svmlight_file(bow_train,y_train[:, c],'train'+str(c+1)+'.dat',zero_based=True, comment=None, query_id=None, multilabel=False)



















