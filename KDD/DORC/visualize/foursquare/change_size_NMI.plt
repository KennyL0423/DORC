set output "change_size_NMI.eps"

set title "(b)NMI"
set xlabel "Data size"
set ylabel "NMI"

plot "change_size_NMI.txt" using 2:xtic(1) title column(1) ls 1 w lp,'' using 3 title column(2) ls 2 w lp,'' using 4 title column(3) ls 3 w lp,'' using 5 title column(4) ls 4 w lp