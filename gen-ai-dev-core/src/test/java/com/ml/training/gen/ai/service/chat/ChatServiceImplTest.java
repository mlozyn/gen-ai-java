package com.ml.training.gen.ai.service.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.ml.training.gen.ai.service.chat.error.ChatNotFoundException;
import com.ml.training.gen.ai.service.chat.mapping.ChatMapper;
import com.ml.training.gen.ai.service.chat.model.Chat;
import com.ml.training.gen.ai.service.chat.repository.ChatRepository;
import com.ml.training.gen.ai.service.chat.repository.entity.ChatEntity;
import com.ml.training.gen.ai.service.common.model.ClientType;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ChatServiceImpl.class, ChatMapper.class})
class ChatServiceImplTest {

  @MockBean
  private ChatRepository repositoryMock;

  @Autowired
  private ChatServiceImpl service;

  @BeforeEach
  public void setUp() {
    when(repositoryMock.findById(any())).thenReturn(Optional.empty());
  }

  @Test
  public void getByIdForExistingEntity() {
    final var entity = ChatEntity.builder()
        .withId(10L)
        .withName("name")
        .withClientType(ClientType.SK_OPEN_AI.getValue())

        .withSystemMessage("system-message")
        .withCreatedAt(Instant.now().getEpochSecond())
        .build();

    when(repositoryMock.findById(eq(entity.getId()))).thenReturn(Optional.of(entity));

    final var actual = service.getById(entity.getId());

    assertChatModel(entity, actual);

    verify(repositoryMock, times(1)).findById(eq(entity.getId()));
    verifyNoMoreInteractions(repositoryMock);
  }

  @Test
  public void getByIdForMissingEntity() {
    final long chatId = 10L;

    assertThrows(ChatNotFoundException.class, () -> service.getById(chatId));

    verify(repositoryMock, times(1)).findById(eq(chatId));
    verifyNoMoreInteractions(repositoryMock);
  }

  @Test
  public void findByIdForExistingEntity() {
    final var entity = ChatEntity.builder()
        .withId(10L)
        .withName("name")
        .withClientType(ClientType.SK_OPEN_AI.getValue())

        .withSystemMessage("system-message")
        .withCreatedAt(Instant.now().getEpochSecond())
        .build();

    when(repositoryMock.findById(eq(entity.getId()))).thenReturn(Optional.of(entity));

    final var actual = service.findById(entity.getId());

    assertTrue(actual.isPresent());
    assertChatModel(entity, actual.get());

    verify(repositoryMock, times(1)).findById(eq(entity.getId()));
    verifyNoMoreInteractions(repositoryMock);
  }

  @Test
  public void findByIdForMissingEntity() {
    final long chatId = 10L;

    final var actual = service.findById(chatId);

    assertTrue(actual.isEmpty());

    verify(repositoryMock, times(1)).findById(eq(chatId));
    verifyNoMoreInteractions(repositoryMock);
  }

  @Test
  public void createWorks() {
    final Chat source = Chat.builder()
        .withName("name")
        .withClientType(ClientType.SK_OPEN_AI)
        .withSystemMessage("system message")
        .build();

    final ChatEntity entity = ChatEntity.builder()
        .withId(20L)
        .withName(source.getName())
        .withClientType(source.getClientType().getValue())
        .withSystemMessage(source.getSystemMessage())
        .withCreatedAt(Instant.now().getEpochSecond())
        .build();

    when(repositoryMock.create(any())).thenReturn(entity);

    final var actual = service.create(source);

    assertChatModel(entity, actual);

    verify(repositoryMock, times(1)).create(any());
    verifyNoMoreInteractions(repositoryMock);
  }

  @Test
  public void deleteWorks() {
    final Long chatId = 20L;

    service.delete(chatId);

    verify(repositoryMock, times(1)).delete(eq(chatId));
    verifyNoMoreInteractions(repositoryMock);
  }

  private void assertChatModel(final ChatEntity entity, final Chat actual) {
    assertNotNull(actual);

    assertEquals(entity.getId(), actual.getId(), "chat id");
    assertEquals(entity.getName(), actual.getName(), "chat name");
    assertEquals(entity.getClientType(), actual.getClientType().getValue(), "chat client type");
    assertEquals(entity.getSystemMessage(), actual.getSystemMessage(), "chat system message");
    assertEquals(entity.getCreatedAt(), actual.getCreatedAt(), "chat created at");
  }

}