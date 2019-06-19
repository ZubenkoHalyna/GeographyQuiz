package geography.quiz

import geography.quiz.Answer.NONE

class Question(val mTextResId: Int, val mAnswerTrue: Boolean, var result: Answer = NONE)