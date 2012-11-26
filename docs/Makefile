all: writeup

writeup: project_writeup.tex
	pdflatex project_writeup.tex
	bibtex project_writeup
	pdflatex project_writeup.tex
	pdflatex project_writeup.tex

.PHONY: clean

clean:
	rm *.aux *.pdf *.bbl *.blg *.log