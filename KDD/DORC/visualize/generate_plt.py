import pandas as pd

dataset_list = [ "foursquare"]
exp_list = {"updateReal": ["eta=8_eps=8E-5", "rate=0.02_eta=8", "rate=0.02_eps=8E-5"],
            "updateExample": ["eta=6_eps=15", "rate=0.03_eps=15", "rate=0.03_eta=6"],
            "foursquare": ["change_size", "eta=500_eps=0.06", "rate=0.02_eps=0.06", "rate=0.02_eta=500"]}
metric_list = ["purity", "NMI", "error", "time"]

xlabel_list = {"updateReal": ["Dirty rate", "Distance threshold", "Density threshold"],
               "updateExample": ["Dirty rate", "Density threshold", "Distance threshold"],
               "foursquare": ["Data size", "Dirty rate", "Density threshold", "Distance threshold"]}

ylabel_list = ["Purity", "NMI", "Error", "Time(s)"]
title_list = ["(a)Clustering purity", "(b)NMI", "(c)Repairing error", "(d)Time cost"]



for dataset in dataset_list:
    fp = open("./" + dataset + "/batch_plot.plt", "w+")
    fp.write("reset\n")
    fp.write("set terminal postscript eps enhanced color\n")
    for k, exp in enumerate(exp_list[dataset]):
        for i, metric in enumerate(metric_list):
            file_name = exp + "_" + metric
            f = open("./" + dataset + "/" + file_name + ".plt", "w+")
            f.write("set output \"" + file_name + ".eps\"\n")
            f.write("\n")
            f.write("set title \"" + title_list[i] + "\"\n")
            f.write("set xlabel \"" + xlabel_list[dataset][k] + "\"\n")
            f.write("set ylabel \"" + ylabel_list[i] + "\"\n")
            if dataset == "foursquare" and metric == "time":
                f.write("set logscale y")
            f.write("\n")
            f.write("plot \"" + file_name + ".txt\" " + "using 2:xtic(1) title column(1) ls 1 w lp,"
                                                        "'' using 3 title column(2) ls 2 w lp,"
                                                        "'' using 4 title column(3) ls 3 w lp,"
                                                        "'' using 5 title column(4) ls 4 w lp")
            f.close()
            fp.write("load \"" + file_name + ".plt\"\n")
    fp.close()
