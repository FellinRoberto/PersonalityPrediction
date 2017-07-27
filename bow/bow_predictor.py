import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
import csv
import numpy as np
import xgboost as xgb

x_train=pd.read_csv('../dataset/X_train.csv', engine='c', encoding='latin-1')
y_train=pd.read_csv('../dataset/y_train.csv', engine='c')

n_features = 5000

tfidf_vectorizer = TfidfVectorizer(max_df=0.95, min_df=0.01, max_features=n_features, stop_words='english')
                                   
print x_train
tfidf = tfidf_vectorizer.fit_transform(x_train['status_update'])

print type(tfidf)
tfidf = tfidf.todense()
print type(tfidf)
print tfidf.shape
#np.savetxt('tfidf.csv', tfidf, delimiter=',')

#tfidf_df = pd.DataFrame(tfidf.toarray())
#tfidf_df.to_csv('tfidf.csv', index=False, quoting=csv.QUOTE_NONNUMERIC, doublequote=True)

