import os
import sys
import random
from itertools import izip
from collections import Counter

def main():
	
	if len(sys.argv) != 3:
		print "Usage: python", sys.argv[0], "<train and test data folder> <number of folds>"
		sys.exit(1)
	
	'''
		Setup paths and number of folds
	'''
	dir = sys.argv[1]
	train_f = dir + "train/svm.train"
	train_res_f = dir + "train/svm.train.res"
	test_f = dir + "test/svm.test"
	test_res_f = dir + "test/svm.test.res"
	n_folds = int(sys.argv[2])
	
	'''
		Set the random seed for reproducibility
	'''
	random_seed = 123	
	random.seed(random_seed)
	
	'''
		Recover question ids
	'''
	qid_train = set([line.strip().split(" ")[0] for line in open(train_res_f)])
	qid_test = set([line.strip().split(" ")[0] for line in open(test_res_f)])
	
	print "Number of questions in the train set:", len(qid_train)
	print "Number of questions in the test set:", len(qid_test)
	
	qids = [qid for qid in qid_train.union(qid_test)]
	n_qids = len(qids)
	
	print "Number of questions:", n_qids
	
	'''
		Setup folds indexes
	'''
	folds = slice_it(qids, n_folds)

	indexes = []
	for index, fold in enumerate(folds):
		print "Questions in fold", index, ":", len(fold)
		indexes.extend([index] * len(fold))
		
	print "Number of indexes:", len(indexes)
	
	'''
		Shuffle indexes in place
	'''
	random.shuffle(indexes)
	
	'''
		Fix associations
	'''
	qid2fold = { qid : fold for qid, fold in zip(qids, indexes)}
	
	'''
		Create fold directories
	'''
	for fold_id in xrange(n_folds):
		directory = dir + "folds/fold-" + str(fold_id) + "/"
		if not os.path.exists(directory):
			os.makedirs(directory)
			
	'''
		Create training and test data
		
		Training examples are written in all folds but the associated fold id
		Thus, test examples are written only in the fold associated with them
	'''
	
	'''
		Open training data output files for each fold id
	'''
	folds_f = {fold_id : open(dir + "folds/fold-" + str(fold_id) + "/svm.train", "w") for fold_id in range(n_folds)}
	folds_res_f = {fold_id : open(dir + "folds/fold-" + str(fold_id) + "/svm.train.res", "w") for fold_id in range(n_folds)}
	
	for example, res in izip(open(train_f, "r"), open(train_res_f, "r")):
		example = example.strip()
		res = res.strip()
		
		'''
			Retrieve the question related to the example and lookup its fold id
		'''
		example_qid = res.split(" ")[0]
		qid_fold = qid2fold[example_qid]
		
		for fold_id in xrange(n_folds):
			if fold_id == qid_fold:
				pass
				#print "[INFO]: Not writing this example in fold:", fold_id, "[", qid_fold, "]"
			else:	
				folds_f[fold_id].write(example + "\n")
				folds_res_f[fold_id].write(res + "\n")
				#print "[INFO]: Writing training examples related to question", example_qid, "in fold", fold_id, "[", qid_fold, "]"
			
	for fold_id in folds_f:
		folds_f[fold_id].close()
		folds_res_f[fold_id].close()
		
	'''
		Open test data output files for each fold id
	'''
	folds_f = {fold_id : open(dir + "folds/fold-" + str(fold_id) + "/svm.test", "w") for fold_id in range(n_folds)}
	folds_res_f = {fold_id : open(dir + "folds/fold-" + str(fold_id) + "/svm.test.res", "w") for fold_id in range(n_folds)}	
	
	for example, res in izip(open(test_f, "r"), open(test_res_f, "r")):
		example = example.strip()
		res = res.strip()
		
		'''
			Retrieve the question related to the example and lookup its fold id
		'''
		example_qid = res.split(" ")[0]
		qid_fold = qid2fold[example_qid]
		
		for fold_id in xrange(n_folds):
			if fold_id == qid_fold: 
				folds_f[fold_id].write(example + "\n")
				folds_res_f[fold_id].write(res + "\n")
				#print "[INFO]: Writing test examples related to question", example_qid, "in fold", fold_id, "[", qid_fold, "]"
			else:
				pass
				#print "[INFO]: Not writing this example in fold:", fold_id, "[", qid_fold, "]"
			
	for fold_id in folds_f:
		folds_f[fold_id].close()
		folds_res_f[fold_id].close()
		
	print_output_stats(dir, n_folds)
		
def print_output_stats(dir, n_folds):
	print "#### SCRIPT OUTPUT"
	
	for fold_id in xrange(n_folds):
		train_qids = [qid.strip().split(" ")[0] for qid in open(dir + "folds/fold-" + str(fold_id) + "/svm.train.res", "r")]
		train_qids = set(train_qids)
		
		test_qids = [qid.strip().split(" ")[0] for qid in open(dir + "folds/fold-" + str(fold_id) + "/svm.test.res", "r")]
		test_qids = set(test_qids)
			
		print "Fold id:", fold_id
		print "\tNumber of questions in train fold:", len(train_qids)
		print "\tNumber of questions in test fold:", len(test_qids)
		print "\tNumber of questions in common:", len(train_qids.intersection(test_qids))
		print ""
		
def slice_it(li, cols=2):
	start = 0
	for i in xrange(cols):
		stop = start + len(li[i::cols])
		yield li[start:stop]
		start = stop

if __name__ == '__main__':	
	main()

