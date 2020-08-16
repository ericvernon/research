import numpy as np
import matplotlib.pyplot as plt

data = np.loadtxt("C:\\Users\\ericv\\code\\research\\MoFGClassifier\\results\\"
                  "2020.08.03.16.56.13"
                  "\\test\\metrics.txt", dtype=np.float32, delimiter=",")
colors = ['red', 'orange', 'yellow', 'green', 'blue', 'pink', 'black']
markers = ['o', '^', 's', '*', 'P', 'x', '2']

data = data[data[:,2] == 1]
plt.scatter(x=data[:,0], y=data[:,1])

plt.xlabel('G-Mean (On Non-Rejected Patterns)')
plt.xlim(0, 1.0)
plt.ylabel('Rejected Patterns')
#plt.ylim(0, 0.7)
plt.show()
