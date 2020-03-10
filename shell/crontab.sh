#!/bin/bash
  for((i=1;i<=30;i++));
          do
           /usr/local/shell/log_generator.sh
          sleep 2
          done &

# 定时任务 crontab -e
# * * * * * crontab.sh