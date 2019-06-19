package geography.quiz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.Button
import android.widget.TextView

class CheatActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_ANSWER_IS_TRUE = "geography.quiz.answer_is_true"
        private const val EXTRA_ANSWER_SHOWN = "geography.quiz.answer_shown"

        private const val EXTRA_ANSWER_IS_TRUE_VALUE = true

        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent
        {
            val intent = Intent(packageContext, CheatActivity::class.java)
            intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            return intent
        }

        fun wasAnswerShown(intent: Intent): Boolean =
            intent.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)
    }

    private var mAnswerIsTrue = EXTRA_ANSWER_IS_TRUE_VALUE
    private lateinit var mAnswerTextView: TextView
    private lateinit var mShowAnswerButton: Button

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        mAnswerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, EXTRA_ANSWER_IS_TRUE_VALUE)
        mAnswerTextView = findViewById(R.id.answer_text_view)
        mShowAnswerButton = findViewById(R.id.show_answer_button)

        mShowAnswerButton.setOnClickListener {
            if (mAnswerIsTrue)
                mAnswerTextView.setText(R.string.true_button)
            else
                mAnswerTextView.setText(R.string.false_button)
            setAnswerShownResult(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val cx = mShowAnswerButton.width / 2
                val cy = mShowAnswerButton.height / 2
                val radius = mShowAnswerButton.width.toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0.toFloat())

                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        mShowAnswerButton.visibility = View.INVISIBLE
                    }
                })

                anim.start()
            } else {
                mShowAnswerButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val intent = Intent()
        intent.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        setResult(RESULT_OK, intent)
    }
}
