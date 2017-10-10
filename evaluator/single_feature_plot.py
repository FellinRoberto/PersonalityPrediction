import pandas as pd
from sklearn.metrics import mean_squared_error
import os
import matplotlib.pyplot as plt

from sklearn.feature_extraction.text import TfidfVectorizer
import csv
import numpy as np
import xgboost as xgb
#bow

mean_predictions=pd.read_csv('./../meanPrediction/y_pred.csv', engine='c')
bow_predictions=pd.read_csv('./../bow/bowPrediction.csv', engine='c', header=None)
true_valuesNew = pd.read_csv('./../minimalpipeline-master/TreeKernel/y_testNew.csv', engine='c',header=None)
#true_values = pd.read_csv('../dataset/y_test.csv', engine='c')

personality_index = 0
n_trains =[100, 500,1000,2000,4000, 10000]
n_test = 5000
results=[]
results_wrong=[]
results_bow=[]
results_bow_wrong=[]
wrong_values = np.roll(true_valuesNew,2, axis=0)
wrong_values = pd.DataFrame(wrong_values)
#wrong=true_valuesNew.ix[1:(n_test-1),personality_index].set_value(10000,2.57)


for i in range(0,len(n_trains)):
    
	n_train = n_trains[i]

	os.system("head -" + str(n_train) + " ../minimalpipeline-master/TreeKernel/train" + str(personality_index+1) + ".dat > ../minimalpipeline-master/TreeKernel/train" + str(personality_index+1) + "b.dat")
	os.system("head -" + str(n_test) + " ../minimalpipeline-master/TreeKernel/test" + str(personality_index+1) + ".dat > ../minimalpipeline-master/TreeKernel/test" + str(personality_index+1) + "b.dat")
	f=os.popen("../svm/src/svm_learn -t 5 -c 1 -C T -z r  ../minimalpipeline-master/TreeKernel/train" + str(personality_index+1)  + "b.dat model"+str(personality_index+1) )
	print f.read()
	os.popen("../svm/src/svm_classify  ../minimalpipeline-master/TreeKernel/test" + str(personality_index+1) + "b.dat model"+str(personality_index+1)+" output"+str(personality_index+1)+".txt")


	#treeKernel_predictions=pd.read_csv('../minimalpipeline-master/TreeKernel/Damiano-train500_test10000/output' + str(personality_index +1 ) +'.txt',engine='c',header=None)
	treeKernel_predictions=pd.read_csv('./output' + str(personality_index +1 ) +'.txt',engine='c',header=None)

	#print "mean error:", mean_squared_error(true_values.ix[:,personality_index], mean_predictions.ix[:,personality_index])
	#print "bow error:", mean_squared_error(true_values.ix[:,personality_index], bow_predictions.ix[:,personality_index])
	#print "TreeKernel error:", mean_squared_error(true_valuesNew.ix[:999,personality_index], treeKernel_predictions.ix[:,0])
	results.append(mean_squared_error(true_valuesNew.ix[:(n_test-1),personality_index], treeKernel_predictions.ix[:,0]))
	results_wrong.append(mean_squared_error(wrong_values.ix[:(n_test-1),personality_index], treeKernel_predictions.ix[:,0]))
	#print true_valuesNew.ix[0:4999,personality_index]
	#print treeKernel_predictions.ix[:,0]
    

	########################################################################################################


	#x_train = pd.read_csv('../dataset/X_train.csv', engine='c', encoding='latin-1')
	#x_test = pd.read_csv('../dataset/X_test.csv', engine='c', encoding='latin-1')
	#y_train = pd.read_csv('../dataset/y_train.csv', engine='c', encoding='latin-1')

	x_train = pd.read_csv('../minimalpipeline-master/TreeKernel/x_trainNew.csv', engine='c', encoding='latin-1',header=None)
	x_test = pd.read_csv('../minimalpipeline-master/TreeKernel/x_testNew.csv', engine='c', encoding='latin-1',header=None)
	y_train = pd.read_csv('../minimalpipeline-master/TreeKernel/y_trainNew.csv', engine='c', encoding='latin-1',header=None)

	x_train=x_train.head(n_train)
	y_train=y_train.head(n_train)
	x_test=x_test.head(n_test)

	n_features = 5000  # numero massimo di parole prese in considerazione in ogni post

	tfidf_vectorizer = TfidfVectorizer(max_df=0.95, min_df=0.01, max_features=n_features, stop_words='english')

	#print x_train
	#bow_train = tfidf_vectorizer.fit_transform(x_train['status_update'])
	bow_train = tfidf_vectorizer.fit_transform(x_train[0])

	bow_train = bow_train.todense()

	#np.savetxt('tfidfTrain.csv', bow_train, delimiter=',')

	#bow_test = tfidf_vectorizer.transform(x_test['status_update'])
	bow_test = tfidf_vectorizer.transform(x_test[0])

	bow_test = bow_test.todense()
	#np.savetxt('tfidfTest.csv', bow_test, delimiter=',')

	# tfidf_df = pd.DataFrame(tfidf.toarray())
	# tfidf_df.to_csv('tfidf.csv', index=False, quoting=csv.QUOTE_NONNUMERIC, doublequote=True)

	y_train = y_train.as_matrix()


	#prediction = np.empty(shape=[1, 0])


		#clf = xgb.XGBRegressor()
	#    clf=xgb.XGBRegressor(learning_rate = 0.1, n_estimators=1000,
	#                           max_depth=5, min_child_weight=1,
	#                           gamma=0, subsample=0.8,
	#                           colsample_bytree=0.8, objective= "reg:linear",
	#                           nthread=-1,scale_pos_weight=1, seed=27)


	clf = xgb.XGBRegressor(learning_rate = 0.2, max_depth = 3)
	y_t = y_train[:, personality_index]
	clf.fit(bow_train, y_t)
	temp =  clf.predict(bow_test)
	#prediction = np.column_stack((prediction, temp))

	results_bow.append(mean_squared_error(true_valuesNew.ix[:(n_test-1),personality_index], temp))
	results_bow_wrong.append(mean_squared_error(wrong_values.ix[:(n_test-1),personality_index], temp))

	######################################################################################################################
	
	
plt.plot(n_trains, results_wrong, 'ro-', label="wrong")
#plt.plot(n_trains, results_bow, 'go-', label="bow")
#plt.plot(n_trains, results_bow_wrong, 'ro-', label="bow_wrong")

#pipeline
plt.plot(n_trains, results, 'bo-', label="treeKernel")
#plt.axis([0, 6, 0, 20])
plt.title('Learning Curve of Personality' + str(personality_index))
plt.legend(loc='upper right')
plt.ylabel('mean square error')
plt.xlabel('number of posts for training')
#plt.plot(x,y,'bo-',markevery=100)
plt.show()
