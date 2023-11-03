set output "rate=0.02_eps=0.06_purity.eps"

set title "(a)Clustering purity"
set xlabel "Density threshold"
set ylabel "Purity"

plot "rate=0.02_eps=0.06_purity.txt" using 2:xtic(1) title column(1) ls 1 w lp,'' using 3 title column(2) ls 2 w lp,'' using 4 title column(3) ls 3 w lp,'' using 5 title column(4) ls 4 w lp