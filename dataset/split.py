import pandas as pd
import csv
import numpy as np
from sklearn.model_selection import train_test_split
from pandas import *


users=pd.read_csv('users_1000.csv', engine='c')
posts=pd.read_csv('posts_1000.csv', engine='c')

x = posts['status_update']

joined_df= pd.merge(posts, users, how='inner', on='userid')
cleaned_df= joined_df.drop(['date_x','date_y', 'blocks', 'item_level'], axis=1)
y = cleaned_df.drop(['userid', 'status_update'], axis=1)

X_train, X_test, y_train, y_test = train_test_split(x, y, test_size=0.2, random_state=94)

X_train = X_train.to_frame()
X_test = X_test.to_frame()

X_train.to_csv('X_train.csv', index=False, quoting=csv.QUOTE_NONNUMERIC, doublequote=True)
X_test.to_csv('X_test.csv', index=False, quoting=csv.QUOTE_NONNUMERIC, doublequote=True)
y_train.to_csv('y_train.csv', index=False, quoting=csv.QUOTE_NONNUMERIC, doublequote=True)
y_test.to_csv('y_test.csv', index=False, quoting=csv.QUOTE_NONNUMERIC, doublequote=True)
