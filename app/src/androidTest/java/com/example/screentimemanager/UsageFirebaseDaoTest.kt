import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.screentimemanager.data.firebase.usage.UsageFirebase
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.google.firebase.database.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.ArgumentCaptor

class UsageFirebaseDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dao: UsageFirebaseDao
    private lateinit var databaseReference: DatabaseReference

    @Before
    fun setUp() {
        databaseReference = mock(DatabaseReference::class.java)
        dao = UsageFirebaseDao(databaseReference)
    }

    @Test
    fun testGetUsageData() {
        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(databaseReference.child(anyString())).thenReturn(databaseReference)
        `when`(databaseReference.addListenerForSingleValueEvent(any())).thenAnswer {
            val valueEventListener = it.getArgument(0, ValueEventListener::class.java)
            valueEventListener.onDataChange(dataSnapshot)
            null
        }

        val observer: Observer<*>? = mock(Observer::class.java)
        dao.usageData.observeForever(observer as Observer<in List<UsageFirebase>>)

        dao.getUsageData("sample@example.com", 1, 1, 2023)

        // Verify that LiveData was updated
        verify(observer).onChanged(any())
    }

    @Test
    fun testSetUsageData() {
        val captor = ArgumentCaptor.forClass(UsageFirebase::class.java)
        `when`(databaseReference.child(anyString())).thenReturn(databaseReference)
        `when`(databaseReference.push()).thenReturn(databaseReference)

        dao.setUsageData("SampleApp", 1, 1, 2023, 3600000L)

        verify(databaseReference).setValue(captor.capture())
        val capturedData = captor.value

        // Add assertions to verify the captured data
        // For example:
        assert(capturedData.appName == "SampleApp")
        assert(capturedData.day == 1)
        assert(capturedData.month == 1)
        assert(capturedData.year == 2023)
        assert(capturedData.usage == 3600000L)
    }
}
