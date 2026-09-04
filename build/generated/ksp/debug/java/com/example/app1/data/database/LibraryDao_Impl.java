package com.example.app1.data.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.app1.domain.model.BookOrigin;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LibraryDao_Impl implements LibraryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BookEntity> __insertionAdapterOfBookEntity;

  private final LuminaConverters __luminaConverters = new LuminaConverters();

  private final EntityInsertionAdapter<LibraryBookEntity> __insertionAdapterOfLibraryBookEntity;

  private final EntityDeletionOrUpdateAdapter<LibraryBookEntity> __deletionAdapterOfLibraryBookEntity;

  public LibraryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBookEntity = new EntityInsertionAdapter<BookEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `books` (`id`,`title`,`author`,`description`,`coverUrl`,`genres`,`targetAudience`,`isIllustrated`,`rating`,`origin`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getAuthor());
        statement.bindString(4, entity.getDescription());
        if (entity.getCoverUrl() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCoverUrl());
        }
        final String _tmp = __luminaConverters.fromStringList(entity.getGenres());
        statement.bindString(6, _tmp);
        statement.bindString(7, entity.getTargetAudience());
        final int _tmp_1 = entity.isIllustrated() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        if (entity.getRating() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getRating());
        }
        final String _tmp_2 = __luminaConverters.fromBookOrigin(entity.getOrigin());
        statement.bindString(10, _tmp_2);
      }
    };
    this.__insertionAdapterOfLibraryBookEntity = new EntityInsertionAdapter<LibraryBookEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `library` (`bookId`,`status`,`addedDate`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LibraryBookEntity entity) {
        statement.bindString(1, entity.getBookId());
        final String _tmp = __luminaConverters.fromReadingStatus(entity.getStatus());
        statement.bindString(2, _tmp);
        statement.bindLong(3, entity.getAddedDate());
      }
    };
    this.__deletionAdapterOfLibraryBookEntity = new EntityDeletionOrUpdateAdapter<LibraryBookEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `library` WHERE `bookId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LibraryBookEntity entity) {
        statement.bindString(1, entity.getBookId());
      }
    };
  }

  @Override
  public Object insertBook(final BookEntity book, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBookEntity.insert(book);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLibraryStatus(final LibraryBookEntity libraryBook,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLibraryBookEntity.insert(libraryBook);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object removeFromLibrary(final LibraryBookEntity libraryBook,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfLibraryBookEntity.handle(libraryBook);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getBookById(final String id, final Continuation<? super BookEntity> $completion) {
    final String _sql = "SELECT * FROM books WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BookEntity>() {
      @Override
      @Nullable
      public BookEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "coverUrl");
          final int _cursorIndexOfGenres = CursorUtil.getColumnIndexOrThrow(_cursor, "genres");
          final int _cursorIndexOfTargetAudience = CursorUtil.getColumnIndexOrThrow(_cursor, "targetAudience");
          final int _cursorIndexOfIsIllustrated = CursorUtil.getColumnIndexOrThrow(_cursor, "isIllustrated");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final BookEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCoverUrl;
            if (_cursor.isNull(_cursorIndexOfCoverUrl)) {
              _tmpCoverUrl = null;
            } else {
              _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
            }
            final List<String> _tmpGenres;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfGenres);
            _tmpGenres = __luminaConverters.toStringList(_tmp);
            final String _tmpTargetAudience;
            _tmpTargetAudience = _cursor.getString(_cursorIndexOfTargetAudience);
            final boolean _tmpIsIllustrated;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsIllustrated);
            _tmpIsIllustrated = _tmp_1 != 0;
            final Double _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            }
            final BookOrigin _tmpOrigin;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfOrigin);
            _tmpOrigin = __luminaConverters.toBookOrigin(_tmp_2);
            _result = new BookEntity(_tmpId,_tmpTitle,_tmpAuthor,_tmpDescription,_tmpCoverUrl,_tmpGenres,_tmpTargetAudience,_tmpIsIllustrated,_tmpRating,_tmpOrigin);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLibraryEntry(final String bookId,
      final Continuation<? super LibraryBookEntity> $completion) {
    final String _sql = "SELECT * FROM library WHERE bookId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, bookId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LibraryBookEntity>() {
      @Override
      @Nullable
      public LibraryBookEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBookId = CursorUtil.getColumnIndexOrThrow(_cursor, "bookId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAddedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "addedDate");
          final LibraryBookEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpBookId;
            _tmpBookId = _cursor.getString(_cursorIndexOfBookId);
            final ReadingStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __luminaConverters.toReadingStatus(_tmp);
            final long _tmpAddedDate;
            _tmpAddedDate = _cursor.getLong(_cursorIndexOfAddedDate);
            _result = new LibraryBookEntity(_tmpBookId,_tmpStatus,_tmpAddedDate);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<LibraryBookEntity>> getAllLibraryEntries() {
    final String _sql = "SELECT * FROM library";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"library"}, new Callable<List<LibraryBookEntity>>() {
      @Override
      @NonNull
      public List<LibraryBookEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBookId = CursorUtil.getColumnIndexOrThrow(_cursor, "bookId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAddedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "addedDate");
          final List<LibraryBookEntity> _result = new ArrayList<LibraryBookEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LibraryBookEntity _item;
            final String _tmpBookId;
            _tmpBookId = _cursor.getString(_cursorIndexOfBookId);
            final ReadingStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __luminaConverters.toReadingStatus(_tmp);
            final long _tmpAddedDate;
            _tmpAddedDate = _cursor.getLong(_cursorIndexOfAddedDate);
            _item = new LibraryBookEntity(_tmpBookId,_tmpStatus,_tmpAddedDate);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<BookEntity>> getBooksByStatus(final ReadingStatus status) {
    final String _sql = "\n"
            + "        SELECT * FROM books \n"
            + "        INNER JOIN library ON books.id = library.bookId \n"
            + "        WHERE library.status = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __luminaConverters.fromReadingStatus(status);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"books",
        "library"}, new Callable<List<BookEntity>>() {
      @Override
      @NonNull
      public List<BookEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "coverUrl");
          final int _cursorIndexOfGenres = CursorUtil.getColumnIndexOrThrow(_cursor, "genres");
          final int _cursorIndexOfTargetAudience = CursorUtil.getColumnIndexOrThrow(_cursor, "targetAudience");
          final int _cursorIndexOfIsIllustrated = CursorUtil.getColumnIndexOrThrow(_cursor, "isIllustrated");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final List<BookEntity> _result = new ArrayList<BookEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BookEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCoverUrl;
            if (_cursor.isNull(_cursorIndexOfCoverUrl)) {
              _tmpCoverUrl = null;
            } else {
              _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
            }
            final List<String> _tmpGenres;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfGenres);
            _tmpGenres = __luminaConverters.toStringList(_tmp_1);
            final String _tmpTargetAudience;
            _tmpTargetAudience = _cursor.getString(_cursorIndexOfTargetAudience);
            final boolean _tmpIsIllustrated;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsIllustrated);
            _tmpIsIllustrated = _tmp_2 != 0;
            final Double _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            }
            final BookOrigin _tmpOrigin;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfOrigin);
            _tmpOrigin = __luminaConverters.toBookOrigin(_tmp_3);
            _item = new BookEntity(_tmpId,_tmpTitle,_tmpAuthor,_tmpDescription,_tmpCoverUrl,_tmpGenres,_tmpTargetAudience,_tmpIsIllustrated,_tmpRating,_tmpOrigin);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
