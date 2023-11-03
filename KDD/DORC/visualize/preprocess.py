import os
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd

df1 = pd.read_table("C:/Users/kenny/Desktop/KDD/DORC/real_test/dbscan_withc.dat", header=None)
df1.columns = ["id", "x", "y", "c"]
df2 = pd.read_table("C:/Users/kenny/Desktop/KDD/DORC/real_test/qdorc_withc.dat", header=None)
df2.columns = ["id", "x", "y", "c"]

plt.figure()
plt.subplot(2,2,4)
plt.scatter(df1["x"], df1["y"], c=df1["c"], s=2)
plt.title("DBSCAN repair")
print("Length of the result of DBSCAN:" + str(df1.shape[0]))

plt.subplot(2,2,3)
plt.scatter(df2["x"], df2["y"], c=df2["c"], s=2)
plt.title("QDORC repair")
print("Length of the result of QDORC:" + str(df2.shape[0]))

df3 = pd.read_table("C:/Users/kenny/Desktop/KDD/DORC/real_test/noise_withc.dat", header=None)
df3.columns = ["id", "x", "y", "c"]
plt.subplot(2,2,1)
plt.scatter(df3["x"], df3["y"], c=df3["c"], s=2)
print("Length of the result of noise_withc: " + str(df3.shape[0]))
plt.title("noise data")

df4 = pd.read_table("C:/Users/kenny/Desktop/KDD/DORC/real_test/truth_withc.dat", header=None)
df4.columns = ["id", "x", "y", "c"]
plt.subplot(2, 2, 2)
plt.scatter(df4["x"], df4["y"], c=df4["c"], s=2)
plt.title("truth data")
print("Length of the result of truth_withc: " + str(df4.shape[0]))
plt.tight_layout()
plt.show()

