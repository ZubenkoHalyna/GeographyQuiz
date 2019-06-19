package geography.quiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.widget.TextView

open class GeographyQuiz : AppCompatActivity() {
    companion object {
        private const val TAG = "QuizActivity"
        private const val KEY_INDEX = "index"
        private const val KEY_TIPS_LEFT = "tips_left"
        private const val REQUEST_CODE_CHEAT = 0
        private val mQuestionBank = arrayOf(
            Question(R.string.question_australia, true),
            Question(R.string.question_oceans, true),
            Question(R.string.question_mideast, false),
            Question(R.string.question_africa, false),
            Question(R.string.question_americas, true),
            Question(R.string.question_asia, true)
        )
    }

    private lateinit var mTrueButton: Button
    private lateinit var mFalseButton: Button
    private lateinit var mNextButton: ImageButton
    private lateinit var mPrevButton: ImageButton
    private lateinit var mQuestionTextView: TextView
    private lateinit var mApiLevelTextView: TextView
    private lateinit var mTipsLeftTextView: TextView
    private lateinit var mCheatButton: Button
    private var mCurrentIndex = 0
    private var mTipsLeft = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle) called")
        setContentView(R.layout.activity_geography_quiz)

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0)
            mTipsLeft = savedInstanceState.getInt(KEY_TIPS_LEFT, 3)
        }

        mTrueButton = findViewById(R.id.true_button)
        mTrueButton.setOnClickListener {
            checkAnswer(true)
        }

        mFalseButton = findViewById(R.id.false_button)
        mFalseButton.setOnClickListener {
            checkAnswer(false)
        }

        mQuestionTextView = findViewById(R.id.question_text_view)
        updateQuestionText()

        mNextButton = findViewById(R.id.next_button)
        mNextButton.setOnClickListener {
            if (checkCompletion()) {
                showResults()
            }
            else {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.size
                updateQuestionText()
                changeButtonsState()
            }
        }

        mPrevButton = findViewById(R.id.prev_button)
        mPrevButton.setOnClickListener {
            if (checkCompletion()) {
                showResults()
            }
            else {
                mCurrentIndex--
                if (mCurrentIndex < 0)
                    mCurrentIndex = mQuestionBank.size - 1
                updateQuestionText()
                changeButtonsState()
            }
        }

        mCheatButton = findViewById(R.id.cheat_button)
        mCheatButton.setOnClickListener {
            if (mTipsLeft > 0) {
                val isAnswerTrue = mQuestionBank[mCurrentIndex].mAnswerTrue
                val intent = CheatActivity.newIntent(this, isAnswerTrue)
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        mTipsLeftTextView = findViewById(R.id.tips_left_text_view)
        updateTipsLeft()

        mApiLevelTextView = findViewById(R.id.api_level_text_view)
        mApiLevelTextView.text = getString(R.string.api_level).format(Build.VERSION.SDK_INT)

        changeButtonsState()
        if (checkCompletion()) {
            showResults()
        }
    }

    private fun updateQuestionText() {
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].mTextResId)
    }

    @SuppressLint("SetTextI18n")
    private fun showResults() {
        val res = mQuestionBank.filter { it.result == Answer.CORRECT }.size * 100 / mQuestionBank.size

        mQuestionTextView.text = getString(R.string.results) + " $res%"
        mFalseButton.visibility = GONE
        mTrueButton.visibility = GONE
        mPrevButton.visibility = GONE
        mNextButton.visibility = GONE
    }

    private fun changeButtonsState() {
        val currentQuestion = mQuestionBank[mCurrentIndex]
        setButtonsEnabledState(false)

        when (currentQuestion.result) {

            Answer.NONE -> setButtonsEnabledState(true)

            Answer.CHEATED -> {
                mCheatButton.background.setTint(getColor(R.color.IncorrectChoiceButtonColor))
                mTrueButton.background.setTint(getColor(R.color.DefaultButtonColor))
                mFalseButton.background.setTint(getColor(R.color.DefaultButtonColor))
            }

            Answer.CORRECT -> {
                if (currentQuestion.mAnswerTrue) {
                    mTrueButton.background.setTint(getColor(R.color.CorrectChoiceButtonColor))
                    mFalseButton.background.setTint(getColor(R.color.DefaultButtonColor))
                    mCheatButton.background.setTint(getColor(R.color.DefaultButtonColor))
                } else {
                    mFalseButton.background.setTint(getColor(R.color.CorrectChoiceButtonColor))
                    mTrueButton.background.setTint(getColor(R.color.DefaultButtonColor))
                    mCheatButton.background.setTint(getColor(R.color.DefaultButtonColor))
                }
            }

            Answer.INCORRECT -> {
                if (currentQuestion.mAnswerTrue) {
                    mFalseButton.background.setTint(getColor(R.color.IncorrectChoiceButtonColor))
                    mTrueButton.background.setTint(getColor(R.color.DefaultButtonColor))
                    mCheatButton.background.setTint(getColor(R.color.DefaultButtonColor))
                } else {
                    mTrueButton.background.setTint(getColor(R.color.IncorrectChoiceButtonColor))
                    mFalseButton.background.setTint(getColor(R.color.DefaultButtonColor))
                    mCheatButton.background.setTint(getColor(R.color.DefaultButtonColor))
                }
            }
        }

        updateTipsLeft()
    }

    private fun checkCompletion(): Boolean {
        mQuestionBank.forEach {
            if (it.result == Answer.NONE)
                return false
        }
        return true
    }

    private fun checkAnswer(userPressedTrue: Boolean) {
        val textId =
            if (mQuestionBank[mCurrentIndex].mAnswerTrue == userPressedTrue) {
                mQuestionBank[mCurrentIndex].result = Answer.CORRECT
                R.string.correct_toast
            }
            else {
                mQuestionBank[mCurrentIndex].result = Answer.INCORRECT
                R.string.incorrect_toast
            }

        changeButtonsState()

        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
    }

    private fun setButtonsEnabledState(state: Boolean) {
        mTrueButton.isEnabled = state
        mFalseButton.isEnabled = state
        mCheatButton.isEnabled = state

        if (state) {
            mTrueButton.background.setTint(getColor(R.color.DefaultButtonColor))
            mFalseButton.background.setTint(getColor(R.color.DefaultButtonColor))
            mCheatButton.background.setTint(getColor(R.color.DefaultButtonColor))
        }
    }

    private fun updateTipsLeft()
    {
        mTipsLeftTextView.text = getString(R.string.tips_left).format(mTipsLeft)
        mCheatButton.isEnabled = mTipsLeft != 0
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CHEAT) {
            data?.let {
                if (CheatActivity.wasAnswerShown(data)) {
                    mQuestionBank[mCurrentIndex].result = Answer.CHEATED
                    mTipsLeft--
                    changeButtonsState()
                }
            }
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle?)
    {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState?.putInt(KEY_INDEX, mCurrentIndex)
        savedInstanceState?.putInt(KEY_TIPS_LEFT, mTipsLeft)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}
