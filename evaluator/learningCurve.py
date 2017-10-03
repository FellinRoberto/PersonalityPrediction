import matplotlib.pyplot as plt
#bow
plt.plot([10, 100, 500, 1000, 2000, 4000], [0.832572425371, 0.675242198493, 0.631077965426, 0.592962735587, 0.574420773068, 0.562277278385], 'ro-', label="bow")
#pipeline
plt.plot([10, 100, 500, 1000, 2000, 4000], [0.622495727626, 0.559529113513, 0.549256571547, 0.553907146227, 0.556276702094, 0.554985966468], 'bo-', label="treeKernel")
#plt.axis([0, 6, 0, 20])
plt.title('Learning Curve with all the personalities together')
plt.legend(loc='upper right')
plt.ylabel('mean square error')
plt.xlabel('number of posts for training')
#plt.plot(x,y,'bo-',markevery=100)
plt.show()



