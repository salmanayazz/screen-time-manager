package com.example.screentimemanager.data.firebase.friend

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class FriendFirebaseDaoTest {

    @Mock
    private lateinit var mockDatabase: FirebaseDatabase

    @Mock
    private lateinit var mockFriendsRef: DatabaseReference

    @Mock
    private lateinit var mockListener: ValueEventListener

    @Mock
    private lateinit var friendFirebaseDao: FriendFirebaseDao

    val friendEmail = "friend@example.com"
    val userEmail = "user@example.com"

    val sanitFriendEmail = "friend(example)com"
    val sanitUserEmail = "user(example)com"

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        `when`(mockDatabase.reference).thenReturn(mockFriendsRef)
        `when`(mockFriendsRef.child(any())).thenReturn(mockFriendsRef)

        friendFirebaseDao = FriendFirebaseDao(mockFriendsRef, userEmail)
    }

    @Test
    fun `sendFriendRequest should update friendsRef with the correct values`(): Unit = runBlocking {
        friendFirebaseDao.sendFriendRequest(friendEmail)

        // check is value was added
        verify(mockFriendsRef.child(sanitFriendEmail).child(sanitUserEmail))
            .setValue(FriendFirebase(sanitFriendEmail, sanitUserEmail, true))
    }

    @Test
    fun `acceptFriendRequest should update friendsRef with the correct values`(): Unit = runBlocking {
        friendFirebaseDao.acceptFriendRequest(friendEmail)

        // verify that isRequest was set to true
        verify(mockFriendsRef.child(sanitUserEmail).child(sanitFriendEmail))
            .setValue(FriendFirebase(sanitUserEmail, sanitFriendEmail, false))
    }

    @Test
    fun `deleteFriend should remove values from friendsRef`(): Unit = runBlocking {
        friendFirebaseDao.deleteFriend(friendEmail)

        val updates = HashMap<String, Any?>()
        updates["/${sanitUserEmail}/${sanitFriendEmail}"] = null
        updates["/${sanitFriendEmail}/${sanitUserEmail}"] = null

        verify(mockFriendsRef, times(1)).updateChildren(updates)
    }
}


