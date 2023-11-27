import android.util.Log
import com.example.screentimemanager.data.firebase.usage.UsageFirebase
import com.google.firebase.database.DatabaseReference
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.ArgumentCaptor
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

class UsageFirebaseDaoTest {
    private lateinit var dao: UsageFirebaseDao
    private lateinit var databaseReference: DatabaseReference

    @Before
    fun setUp() {
        // Initialize the UsageFirebaseDao with a mocked DatabaseReference
        databaseReference = mock(DatabaseReference::class.java)
        dao = UsageFirebaseDao(databaseReference)
    }

    @Test
    suspend fun testGetUsageData() {
        // Mock DatabaseReference to return a sample data snapshot
        val dataSnapshot = mock(DataSnapshot::class.java)
        `when`(databaseReference.child(anyString())).thenReturn(databaseReference)
        `when`(databaseReference.addListenerForSingleValueEvent(any())).thenAnswer {
            val valueEventListener = it.getArgument(0, ValueEventListener::class.java)
            valueEventListener.onDataChange(dataSnapshot)
            null
        }

        // Call the getUsageData method with sample input
        val email = "sample@example.com"
        val day = 1
        val month = 1
        val year = 2023
        dao.getUsageData(email, day, month, year)
        // Assert the dao returned the correct data

    }

    @Test
    suspend fun testSetUsageData() {
        // Capture data passed to setValue
        val captor = ArgumentCaptor.forClass(UsageFirebase::class.java)
        `when`(databaseReference.child(anyString())).thenReturn(databaseReference)
        `when`(databaseReference.push()).thenReturn(databaseReference)

        // Call the setUsageData method with sample input
        val appName = "SampleApp"
        val day = 1
        val month = 1
        val year = 2023
        val usage = 3600000L // 1 hour
        dao.setUsageData(appName, day, month, year, usage)

        // Verify that the correct data was passed to setValue
        verify(databaseReference).setValue(captor.capture())
        val capturedData = captor.value

        // Assert that the captured data matches the expected data
        // Add your assertions here
    }
}
