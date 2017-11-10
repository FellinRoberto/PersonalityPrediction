import matplotlib.pyplot as plt
import numpy as np

#MultiPost
#plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.9562, 0.9562, 0.9562, 0.9562, 0.9562, 0.9562, 0.9562], 'ro-', label="Openness")
#plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.8812, 0.8812, 0.8812, 0.8812, 0.8812, 0.8812, 0.8812], 'bo-', label="Conscientiousness")
#plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.8662, 0.8662, 0.8662, 0.8662, 0.8662, 0.8662, 0.8662], 'yo-', label="Extraversion")
#plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.9062, 0.9062, 0.9062, 0.9062, 0.9062, 0.9062, 0.9062], 'go-', label="Agreebleness")
#plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.5502, 0.5510, 0.5510, 0.5504, 0.5506, 0.5506, 0.5498], 'ko-', label="Neuroticism")

#SinglePost
plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.9558, 0.9544, 0.9556, 0.9560, 0.9558, 0.9552, 0.9550], 'ro-', label="Openness")
plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.8646, 0.8674, 0.8670, 0.8664, 0.8662, 0.8646, 0.8628], 'bo-', label="Conscientiousness")
plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.8778, 0.8772, 0.8778, 0.8772, 0.8762, 0.8758, 0.8756], 'yo-', label="Extraversion")
plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.9076, 0.9054, 0.9068, 0.9076, 0.9080, 0.9068, 0.9070], 'go-', label="Agreebleness")
plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.5746, 0.5870, 0.5618, 0.5648, 0.5412, 0.5396, 0.5442], 'ko-', label="Neuroticism")


plt.title('Learning Curve Classification with threshold 2.5')
plt.legend(loc='upper right')
plt.ylabel('accuracy')
plt.yticks(np.arange(0.55, 1, 0.05))
plt.xlabel('number of posts for training')
#plt.plot(x,y,'bo-',markevery=100)
plt.show()



