package edu.kingston.agriconnect.repository;


import edu.kingston.agriconnect.model.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :userId1 AND m.recipientId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.recipientId = :userId1) " +
            "ORDER BY m.sentTime ASC")
    Page<Message> findConversationBetweenUsers(@Param("userId1") Long userId1,
                                               @Param("userId2") Long userId2,
                                               Pageable pageable);

    // Optimized latest messages with pagination and indexing
    @Query("SELECT m FROM Message m " +
            "WHERE (m.senderId = :userId OR m.recipientId = :userId) " +
            "AND m.sentTime = (SELECT MAX(m2.sentTime) FROM Message m2 " +
            "WHERE (m2.senderId = m.senderId AND m2.recipientId = m.recipientId) OR " +
            "(m2.senderId = m.recipientId AND m2.recipientId = m.senderId))")
    Page<Message> findLatestMessagesForUser(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE " +
            "(m.senderId = :userId1 AND m.recipientId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.recipientId = :userId1)")
    int deleteConversationBetweenUsers(@Param("userId1") Long userId1,
                                       @Param("userId2") Long userId2);

    int countByRecipientIdAndSenderIdAndReadFalse(Long userId, Long otherUserId);
}