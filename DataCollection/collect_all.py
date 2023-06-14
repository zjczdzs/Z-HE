import requests
from bs4 import BeautifulSoup
import pandas as pd
import re
import jieba

dataf = pd.read_excel('D:\python\spider\engineering\总数据.xlsx')
title = dataf['标题']
net = dataf['标题链接']
content = []
author = []
al = []
describe = []
# print(dataf.head())
num = 0
for link_href in net:
    try:
        text = ''
        res = requests.get(link_href)
        res.encoding = res.apparent_encoding  # 根据网站的编码方式修改解码方式
        soup = BeautifulSoup(res.text, 'html5lib')
        # 获取author
        source = soup.select(".source")
        data = soup.select('p')
        for s in source:
            author_data = s.text.strip()
        author.append(author_data)
        # 获取context及describe
        data = soup.select('p')
        for p in data:
            text += p.text.strip()
            result = text.split('。')[0]
        content.append(text)
        describe.append(result)
        num += 1
        print(num)
    except:
        pass
# 获取标题内容
title = title[:len(content)]
# 使用dataframe来进行存储
total_data = {'author':author,'title': title,'content': content,'describe':describe}
data = pd.DataFrame(total_data)
# 提取来源名
data['author1'] = data['author'].str.split('：',expand=True)[1]
data['author'] = data['author1'].values
del data['author1']
# 删除有空值行
data = data.dropna(axis=0, how='any')
# 保存成EXCEL格式
data.to_excel('总数据.xlsx')
