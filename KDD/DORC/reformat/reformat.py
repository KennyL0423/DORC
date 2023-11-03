import os

import pandas as pd

size_list = [20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000,
             150000, 200000, 250000, 300000, 350000, 400000]
rate_list = [0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1,
             0.11, 0.12, 0.13, 0.14, 0.15, 0.16, 0.17, 0.18, 0.19]

# for size in size_list:
#     fr = open("../data/foursquare/"+str(size)+"/noiseData.dat", "r+")
#     fw = open("../data/foursquare/"+str(size)+"/newNoiseData.dat", "w+")
#     lines = fr.readlines()
#     for i, line in enumerate(lines):
#         words = line.split('\t')
#         fw.write(str(i)+"\t"+words[0]+"\t"+words[1]+"\n")
#     fw.close()
#     fr.close()
#
# for rate in rate_list:
#     fr = open("../data/foursquare/100000/"+str(rate)+"/noiseData.dat", "r+")
#     fw = open("../data/foursquare/100000/"+str(rate)+"/newNoiseData.dat", "w+")
#     lines = fr.readlines()
#     for i, line in enumerate(lines):
#         words = line.split('\t')
#         fw.write(str(i)+"\t"+words[0]+"\t"+words[1]+"\n")
#     fw.close()
#     fr.close()

for rate in rate_list:
    fr = open("../data/foursquare/100000/"+str(rate)+"/truthData.dat", "r+")
    fw = open("../data/foursquare/100000/"+str(rate)+"/newTruthData.dat", "w+")
    lines = fr.readlines()
    for i, line in enumerate(lines):
        words = line.split('\t')
        fw.write(str(i)+"\t"+words[0]+"\t"+words[1]+"\n")
    fw.close()
    fr.close()
    os.rename("../data/foursquare/100000/"+str(rate)+"/truthData.dat", "../data/foursquare/100000/"+str(rate)+"/oldTruthData.dat")
    os.rename("../data/foursquare/100000/"+str(rate)+"/newTruthData.dat","../data/foursquare/100000/"+str(rate)+ "/truthData.dat")

