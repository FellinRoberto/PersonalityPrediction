import matplotlib.pyplot as plt
#bow
plt.plot([500, 1000, 2000, 4000, 8000, 18000], [0.650593405119,0.631781876476,0.612408550249,0.598554536745,0.589350747735,0.585818753687], 'ro-', label="bow")
#pipeline
plt.plot([500, 1000, 2000, 4000, 8000, 18000], [0.606446917787,0.605181734254,0.605831208235,0.607114050747,0.605549739529,0.605255194522], 'bo-', label="treeKernel")
#plt.axis([0, 6, 0, 20])
plt.title('Learning Curve')
plt.legend(loc='upper right')
plt.ylabel('mean square error')
plt.xlabel('number of posts for training')
#plt.plot(x,y,'bo-',markevery=100)
plt.show()



