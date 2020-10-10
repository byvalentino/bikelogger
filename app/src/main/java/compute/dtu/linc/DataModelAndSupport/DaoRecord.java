package compute.dtu.linc.DataModelAndSupport;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//Intermediary class between user and database, contains SQL operation definitions
@Dao
public interface DaoRecord {
    @Insert
    void insertSingleRecord (Record record);

    @Query("SELECT * FROM Record WHERE pk = :recordPK")
    Record fetchRecord (int recordPK);

    @Query("SELECT * FROM Record")
    List<Record> fetchAllTasks();


    @Query("DELETE FROM Record")
    void deleteAll();

    @Query("DELETE FROM Record WHERE timeStamp = :timestamp")
    void deleteRecord(Long timestamp);

    @Query("SELECT COUNT(*) FROM Record")
    Integer getRowCount();

    @Update
    void updateRecord (Record record);

    @Delete
    void deleteRecord (Record record);

}
