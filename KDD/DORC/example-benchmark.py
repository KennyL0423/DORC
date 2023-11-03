import math
import random
import time

import numpy as np
import pandas as pd
from scipy.spatial import KDTree
from sklearn.metrics import normalized_mutual_info_score
from sklearn.cluster import DBSCAN

class Visitlist:
    def __init__(self, num=0):
        self.unvisited_list = [i for i in range(num)]
        self.visited_list = list()
        self.unvisited_num = num

    def visit(self, pointId):
        self.unvisited_list.remove(pointId)
        self.visited_list.append(pointId)
        self.unvisited_num -= 1


class Benchmark:
    def __init__(self, pred_path, truth_path, eps, eta):
        self.pred_array = np.array(pd.read_table(pred_path, header=None))
        self.truth_array = np.array(pd.read_table(truth_path, header=None))

        self.pred_len = self.pred_array.shape[0]
        self.truth_len = self.truth_array.shape[0]

        self.pred_cluster = [-1 for i in range(self.pred_len)]
        self.truth_cluster = [-1 for i in range(self.truth_len)]
        self.pred_cluster_num = 0
        self.truth_cluster_num = 0

        self.eps = eps
        self.eta = eta

    def run(self):
        self.pred_cluster = DBSCAN(eps=self.eps, min_samples=self.eta).fit_predict(self.pred_array[:, [1, 2]])
        self.truth_cluster = DBSCAN(eps=self.eps, min_samples=self.eta).fit_predict(self.truth_array[:, [1, 2]])
        self.pred_cluster_num = np.unique(self.pred_cluster).size
        self.truth_cluster_num = np.unique(self.truth_cluster).size


    def dbscan(self):
        pred_vlist = Visitlist(self.pred_len)
        pred_tuples = self.pred_array[:, [1, 2]]
        kd = KDTree(pred_tuples)

        cnt = -1
        while pred_vlist.unvisited_num > 0:
            p = pred_vlist.unvisited_list[-1]
            pred_vlist.visit(p)

            neighbors = kd.query_ball_point(pred_tuples[p], self.eps)

            if len(neighbors) >= self.eta:
                cnt += 1
                self.pred_cluster[p] = cnt
                for p1 in neighbors:
                    if p1 in pred_vlist.unvisited_list:
                        pred_vlist.visit(p1)
                        neighbors1 = kd.query_ball_point(pred_tuples[p1], self.eps)
                        if len(neighbors1) >= self.eta:
                            for i in neighbors1:
                                if not np.isin(i, neighbors):
                                    neighbors.append(i)
                        if self.pred_cluster[p1] == -1:
                            self.pred_cluster[p1] = cnt
            else:
                self.pred_cluster[p] = -1
        self.pred_cluster_num = cnt + 1

        truth_vlist = Visitlist(self.truth_len)
        truth_tuples = self.truth_array[:, [1, 2]]
        kd = KDTree(truth_tuples)
        cnt = -1
        while truth_vlist.unvisited_num > 0:
            p = random.choice(truth_vlist.unvisited_list)
            truth_vlist.visit(p)

            neighbors = kd.query_ball_point(truth_tuples[p], self.eps)
            if len(neighbors) >= self.eta:
                cnt += 1
                self.truth_cluster[p] = cnt
                for p1 in neighbors:
                    if p1 in truth_vlist.unvisited_list:
                        truth_vlist.visit(p1)
                        neighbors1 = kd.query_ball_point(truth_tuples[p1], self.eps)
                        if len(neighbors1) >= self.eta:
                            for i in neighbors1:
                                if not np.isin(i, neighbors):
                                    neighbors.append(i)
                        if self.truth_cluster[p1] == -1:
                            self.truth_cluster[p1] = cnt
            else:
                self.truth_cluster[p] = -1
        self.truth_cluster_num = cnt + 1

    def logD(self):
        f = open("./pred.dat", "w")
        for i, tuple in enumerate(self.pred_array):
            f.write(str(int(tuple[0])) + "\t" + str(tuple[1]) + "\t" + str(tuple[2]) +
                    "\t" + str(self.pred_cluster[i]) + "\n")
        f.close()

        f = open("./truth.dat", "w")
        for i, tuple in enumerate(self.truth_array):
            f.write(str(int(tuple[0])) + "\t" + str(tuple[1]) + "\t" + str(tuple[2]) +
                    "\t" + str(self.truth_cluster[i]) + "\n")
        f.close()

    def calPurity(self):
        cnt_matrix = np.zeros([self.pred_cluster_num + 1, self.truth_cluster_num + 1])
        for i in range(self.pred_len):
            cnt_matrix[self.pred_cluster[i] + 1][self.truth_cluster[i] + 1] += 1
        purity_cnt = 0
        for i in range(self.pred_cluster_num + 1):
            maxv = 0
            for j in range(self.truth_cluster_num + 1):
                if cnt_matrix[i][j] > maxv:
                    maxv = cnt_matrix[i][j]
            purity_cnt += maxv
        return purity_cnt / self.pred_len

    def calNMI(self):
        if self.pred_len != self.truth_len:
            adopted_pred_cluster = [-1 for i in range(self.truth_len)]
            pointer = 0
            counter = -1
            for i in range(self.truth_len):
                if pointer >= self.pred_len:
                    adopted_pred_cluster[i] = counter
                    counter -= 1
                else:
                    if self.pred_array[pointer][0] != i:
                        adopted_pred_cluster[i] = counter
                        counter -= 1
                    else:
                        adopted_pred_cluster[i] = self.pred_cluster[pointer]
                        pointer += 1
            return normalized_mutual_info_score(self.truth_cluster, adopted_pred_cluster)
        return normalized_mutual_info_score(self.truth_cluster, self.pred_cluster)

    def calError(self):
        if self.pred_len!=self.truth_len:
            return None

        res = 0
        for i in range(self.pred_len):
            res += math.sqrt((self.pred_array[i][1] - self.truth_array[i][1]) ** 2 + (
                        self.pred_array[i][2] - self.truth_array[i][2]) ** 2)
        return res

    def log(self, log_path):
        f = open(log_path, "a+")
        f.write(str(self.calPurity()) + " " + str(self.calNMI()) + " " + str(self.calError())+"\n")
        f.close()


if __name__ == "__main__":
    rate_list = [0.015, 0.03, 0.045, 0.06, 0.075, 0.09, 0.105, 0.12, 0.135, 0.15, 0.165, 0.18]

    method_list = ["QDORC", "LDORC", "GDORC"]

    for method in method_list:
        predPath = "./outputdata/updateExample/" + method + "/change_size/"
        for size in rate_list:
            purity = 0.0
            nmi = 0.0
            error = 0.0
            bm = Benchmark(predPath + str(size) +"/repairData.dat",
                           "./data/updateExample/" + str(size) + "/truthData.dat",
                           eta=500, eps=0.06)
            bm.run()
            bm.log(predPath + "benchmark.dat",)
            
            print(method + " exp1 " + str(size) + " finished.")

        
