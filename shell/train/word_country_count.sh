#!/usr/bin/env bash

read -r -p "Delete result/word_country_count? [Y/n] " input

case ${input} in
    [yY][eE][sS]|[yY])
        hadoop fs -rm -r result/word_country_count
		hadoop jar \
            ././../../target/NaiveBayesClassifier-1.0.0.jar \
            io.github.trierbo.train.WordCountryCount \
            train result/word_country_count
            ;;
    *)
	exit 1
	;;
esac
