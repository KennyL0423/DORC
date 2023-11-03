import pandas as pd


dataset_list = ["foursquare"]
method_list = {"updateReal": ["QDORC", "LDORC", "GDORC", "DBSCAN"],
               "updateExample": ["QDORC", "LDORC", "GDORC", "DBSCAN"],
               "foursquare": ["QDORC", "LDORC", "GDORC"]}
exp_list = {"updateReal": ["eta=8_eps=8E-5", "rate=0.02_eta=8", "rate=0.02_eps=8E-5"],
            "updateExample": ["eta=6_eps=15", "rate=0.03_eps=15", "rate=0.03_eta=6"],
            "foursquare": ["change_size", "eta=500_eps=0.06", "rate=0.02_eps=0.06", "rate=0.02_eta=500"]}

parameter_dict = {"updateReal": [[0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18],
                                 [5E-5, 6E-5, 7E-5, 8E-5, 9E-5, 1E-4, 1.1E-4, 1.2E-4, 1.3E-4, 1.4E-4, 1.5E-4],
                                 [5, 6, 7, 8, 9, 10, 11, 12, 13, 14]],
                  "updateExample": [[0.015, 0.03, 0.045, 0.06, 0.075, 0.09, 0.105, 0.12, 0.135, 0.15, 0.165, 0.18],
                                    [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
                                    [11, 12, 13, 14, 15, 16, 17, 18, 19, 100]],
                  "foursquare": [[50000, 100000, 150000, 200000, 250000, 300000, 350000, 400000],
                                 [0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18],
                                 [25, 50, 100, 300, 500, 700, 1000, 1200],
                                 [0.02, 0.04, 0.06, 0.08, 0.1, 0.12, 0.14, 0.16, 0.18]]}

for dataset in dataset_list:
    for i, exp in enumerate(exp_list[dataset]):
        df_purity = pd.DataFrame(columns=method_list[dataset])
        df_nmi = pd.DataFrame(columns=method_list[dataset])
        df_error = pd.DataFrame(columns=method_list[dataset])
        df_time = pd.DataFrame(columns=method_list[dataset])

        for method in method_list[dataset]:
            df_tmp = pd.read_csv("../outputdata/"+dataset+"/"+method+"/"+exp+"/"+"benchmark.dat", header=None, sep=' ')
            df_tmp.columns = ["purity", "NMI", "error"]

            df_tmp_time = pd.read_csv("../outputdata/"+dataset+"/"+method+"/"+exp+"/"+method+"_time.txt", header=None)
            df_tmp_time.columns = ["time"]

            df_purity[method] = df_tmp["purity"]
            df_nmi[method] = df_tmp["NMI"]
            df_time[method] = df_tmp_time["time"]
            if method != "DBSCAN":
                df_error[method] = df_tmp["error"]
        print(dataset, exp)
        df_purity.index = parameter_dict[dataset][i]
        df_nmi.index = parameter_dict[dataset][i]
        df_error.index = parameter_dict[dataset][i]
        df_time.index = parameter_dict[dataset][i]

        df_purity.to_csv("./"+dataset+"/"+exp+"_purity.txt", sep='\t', index=True, header=True)
        df_nmi.to_csv("./" + dataset + "/" + exp + "_nmi.txt", sep='\t', index=True, header=True)
        df_error.to_csv("./" + dataset + "/" + exp + "_error.txt", sep='\t', index=True, header=True)
        df_time.to_csv("./" + dataset + "/" + exp + "_time.txt", sep='\t', index=True, header=True)
