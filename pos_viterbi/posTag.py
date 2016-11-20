from __future__ import division
import sys


def createWordList():
    infile = open(sys.argv[1])
    wordCount = dict()
    possibleTag = dict()
    f = infile.readlines()
    for lines in f:
        words = lines.split()
        for word in words:
            x = word.split('_')
            x[0] = x[0].lower()
            possibleTag[x[0]] = possibleTag.get(x[0], []) + [x[1]]
            x = '_'.join(x)
            wordCount[x] = wordCount.get(x, 0) + 1
    infile.close()
    probableTag = dict()
    for word in possibleTag.keys():
        count = 0
        checktag = 'NN'
        for tag in set(possibleTag[word]):
            x = word + '_' + tag
            if count < wordCount[x]:
                count = wordCount[x]
                checktag = tag
        probableTag[word] = checktag
    wordList = []
    for lines in f:
        words = lines.split()
        for word in words:
            x = word.split('_')
            wordList += [(x[0], x[1], probableTag[x[0].lower()])]
    return wordList


def checkErrorRate(wordList):
    '''
     it will return the error rate , wordList will contain the tuple which will have
     word, expected token and actual token present.
    :param wordList: have tuple containing word, expected word, actual word
    :return: error rate
    '''
    error = 0
    for word, expected, actual in wordList:
        if expected != actual:
            error += 1
    return error / len(wordList)


def topErrorWords(wordList, n=5):
    '''
     it will return the top 5 words, which has highest error rate,
    :param wordList: Will have
    :param n: number of required error words
    :return: list of words
    '''
    error = dict()
    for word, expected, actual in wordList:
        if expected != actual:
            error[word] = error.get(word, 0) + 1
    topwords = sorted(error, key=lambda x: error.get(x, 0), reverse=True)
    return topwords[:5]


def applyRule1(wordList):
    '''
        Rule 1: if next of 'that' starts with VB, then tag it as WDT.
    '''
    for i in range(len(wordList) - 1):
        if (wordList[i][0] == 'that') and (wordList[i + 1][2].startswith('VB')):
            wordList[i] = (wordList[i][0], wordList[i][1], 'WDT')
    return wordList


def applyRule2(wordList):
    '''
        Rule 2: if previous of 'that' is IN, then tag it as DT
    '''
    for i in range(1, len(wordList)):
        if (wordList[i][0] == 'that') and (wordList[i - 1][2] == 'IN'):
            wordList[i] = (wordList[i][0], wordList[i][1], 'DT')
    return wordList


def applyRule3(wordList):
    '''
        Rule 3: if 'New' and previous is not '.', then tag it as NNP
    '''
    for i in range(1, len(wordList)):
        if wordList[i][0] == 'New' and wordList[i - 1][0] != '.':
            wordList[i] = (wordList[i][0], wordList[i][1], 'NNP')
    return wordList


def applyRule4(wordList):
    '''
        Rule 4: if previous of "'s" is PRP/DT/EX, then tag it as VBZ
    '''
    for i in range(1, len(wordList)):
        if wordList[i][0] == "'s":
            a = wordList[i - 1][2]
            if (a in ['PRP', 'DT', 'EX', 'WDT']):
                wordList[i] = (wordList[i][0], wordList[i][1], 'VBZ')
    return wordList


def applyRule5(wordList):
    '''
        Rule 5: if previous of 'plans' starts with VB, then tag it as NNS
    '''
    for i in range(1, len(wordList)):
        if wordList[i][0] == "plans":
            a = wordList[i - 1][2]
            if a.startswith('VB'):
                wordList[i] = (wordList[i][0], wordList[i][1], 'NNS')
    return wordList


def writeToOutput(wordList, number=0):
    out = 'output' + str(number) + '.txt'
    op = open(out, 'w')
    word, expected, actual = wordList[0]
    op.write(word + '_' + actual)
    for word, expected, actual in wordList[1:]:
        op.write(' ')
        op.write(word)
        op.write('_')
        op.write(actual)
    op.close()


wordList = createWordList()
print("Initial error rate = ")
print(checkErrorRate(wordList))
print('Top 5 error words are : "' + '", "'.join(topErrorWords(wordList)) + '"')

wordList1 = applyRule1(wordList)
print("Error rate after applying rule 1 = ")
print(checkErrorRate(wordList1))
wordList = applyRule2(wordList)
print("Error rate after applying rule 2 =")
print(checkErrorRate(wordList))
wordList = applyRule3(wordList)
print("Error rate after applying rule 3 =")
print(checkErrorRate(wordList))
wordList = applyRule4(wordList)
print("Error rate after applying rule 4 =")
print(checkErrorRate(wordList))
wordList = applyRule5(wordList)
print("Error rate after applying rule 5 =")
print(checkErrorRate(wordList))
