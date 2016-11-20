#kunal krishna kxk155230
import nltk
from nltk.corpus import wordnet as wn


bank_sentence = 'The bank can guarantee deposits will eventually cover future tuition costs because it invests in adjustable-rate mortgage securities.'


def lesk(context, ambiguous_word):
    max_overlaps = 0; lesk_senses = None
    i = 1
    for ss in wn.synsets(ambiguous_word):
        context_sentence = context.split()
        # Includes definition.
        print(str(i) + '.', )
        print(ss.definition())
        lesk_dictionary = ss.definition().split()
        # Includes Example
        if ss.examples() != []:
            print('\t Examples are')
        else:
            print('\t\tNo Examples present.')
        for x in ss.examples():
            print('\t\t' + x)
            lesk_dictionary += x.split()
        overlaps = set(lesk_dictionary).intersection(context_sentence)
        #  Area to print the tokens in a nice format. #
        lesk_dictionary = list(lesk_dictionary)
        # Any overlapping word will be seen in {word} format.
        for word in overlaps:
            index = 0
            for words in lesk_dictionary:
                if words == word:
                    lesk_dictionary[index] = '{'+lesk_dictionary[index]+'}'
                index += 1
            index = 0
            for words in context_sentence:
                if words == word:
                    context_sentence[index] = '{' + context_sentence[index] + '}'
                index += 1
        # Now we need to print the sentence and Dict
        print('\t\t///words in Gloss and Examples which has overlap word  ////    ')
        print('\t\t' + ', '.join(list(lesk_dictionary)))
        print('\t\t ///words in Context////        :')
        print('\t\t' + ', '.join(list(context_sentence)))
        word = ' words  ' if len(overlaps) != 1 else ' word'
        print('\t' + str(len(overlaps)) + word + ' overlap found', )
        if len(overlaps) != 0:
            if len(overlaps) == 1:
                x = 'which is'
            else:
                x = 'which are'
            print(x, )
            print("'" + "', '".join(overlaps) + "'")
        # Final calculation based on the overlaps of words
        if len(overlaps) > max_overlaps:
            print(lesk_senses)
            lesk_senses = ss
            max_overlaps = len(overlaps)
        i += 1
        print('')
    return lesk_senses

print("Context:", bank_sentence)
Result = lesk(bank_sentence, 'bank')
print("Best Sense:", Result)
print("Also Best Sense Definition:", Result.definition())
print("")