import matplotlib.pyplot as plt
#bow
plt.plot([100, 500, 2000, 5000, 10000, 20000, 50000], [0.675242198493, 0.631077965426, 0.574420773068, 0.560408399209, 0.555314703364, 0.55178173127, 0.549324278773], 'ro-', label="bow")
#pipeline
plt.plot([100, 500, 2000, 5000, 10000, 20000, 50000], [0.566965476198, 0.565609409608, 0.568659041335, 0.568387885969, 0.56801183582, 0.568495795651, 0.567979065016], 'bo-', label="treeKernel")
#plt.axis([0, 6, 0, 20])
plt.title('Learning Curve')
plt.legend(loc='upper right')
plt.ylabel('mean square error')
plt.xlabel('number of posts for training')
#plt.plot(x,y,'bo-',markevery=100)
plt.show()



