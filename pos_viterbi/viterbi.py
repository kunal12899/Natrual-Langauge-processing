
def viterbi(hiddenStates, initialFreq, observationSeq, stateTransitionProb, observationSProbability):
    totalStates = len(hiddenStates)
    loop = range(totalStates)
    maxProb = []
    cause = []
    stateSequence = ''
    for i in observationSeq:
        maxProb += [[0]*totalStates]
        cause += [[0]*totalStates]

    # we need to calculate max probability at start and initialize cause to 0

    for i in loop:
        maxProb[0][i] = initialFreq[i]*observationSProbability[i][int(observationSeq[0])-1]
        # hot or cold start probability
        cause[0][i] = -1

    # calculate max probability at each step and which transition causes this for each state

    for obs in range(1,len(observationSeq)):
        for current in loop:
            for previous in loop:
                calc = maxProb[obs-1][previous]*stateTransitionProb[previous][current]*observationSProbability[current][int(observationSeq[obs])-1]
                if calc > maxProb[obs][current]:
                    maxProb[obs][current] = calc
                    cause[obs][current] = previous

    # end would be the max probability

    index = 0
    prob = 0
    for state in loop:
        if prob < maxProb[-1][state]:
            prob = maxProb[-1][state]
            index = state
            stateSequence = hiddenStates[index]

    # check previous node for each probability

    for observation in range(len(observationSeq)-1, -1, -1):
        index = cause[observation][index]
        if index != -1:
            stateSequence += hiddenStates[index]
    return stateSequence[::-1], prob

observationSequences = ['331122313', '331123312']
states =['H', 'C']
initial = [0.8, 0.2]
stateChangeProb = [[0.7, 0.3], [0.4, 0.6]]
observationProb = [[0.2, 0.4, 0.4], [0.5, 0.4, 0.1]]
for sequence in observationSequences:
    a = 'Most Likely weather sequence for observation '+sequence+' is "'
    x = viterbi(states, initial, sequence, stateChangeProb, observationProb)
    a += x[0]+'" with a probability of '+str(x[1])
    print(a)
