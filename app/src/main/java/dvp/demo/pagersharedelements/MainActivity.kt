package dvp.demo.pagersharedelements

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFragmentOne()
    }

    private fun initFragmentOne(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, FragmentOne(), FragmentOne::class.java.simpleName)
            .commit()
    }

}
