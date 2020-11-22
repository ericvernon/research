import numpy as np
import matplotlib.pyplot as plt

colors = ['red', 'orange', 'yellow', 'green', 'blue', 'pink', 'black']
markers = ['o', '^', 's', '*', 'P', 'x', '2']

folder = "C:\\Users\\Eric\\code\\research\\RejectOption\\results\\2020.10.26.13.21.40\\train\\"

GEN = "25"

data = np.loadtxt(folder + "gen" + GEN + ".txt")
plt.scatter(x=data[:, 1], y=data[:, 0])
plt.ylim(0, 1.0)
plt.ylabel('1 - G-Mean')
plt.xlabel('# Rules')
plt.title("Dataset: Pima.  Generations: " + GEN)
plt.show()

# plt.xlim(0, 1.0)
# plt.legend(
#     ['Per-Class Variable Threshold', 'Single Variable Threshold', 'Static Threshold = 0.0', 'Static Threshold = 0.2']
# )
# plt.show()
