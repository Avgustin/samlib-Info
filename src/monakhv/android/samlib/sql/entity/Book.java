/*
 * Copyright 2013 Dmitry Monakhov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package monakhv.android.samlib.sql.entity;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import monakhv.android.samlib.data.DataExportImport;
import monakhv.android.samlib.exception.BookParseException;

/**
 *
 * @author monakhv
 */
public class Book implements Serializable {

    public static final int SELECTED_GROUP_ID=1;
    private static final String SPLIT = "\\|";
    private static final int BOOK_LINK = 0;
    private static final int BOOK_AUTHOR = 1;
    private static final int BOOK_TITLE = 2;
    private static final int BOOK_FORM = 3;
    private static final int BOOK_SIZE = 4;
    private static final int BOOK_DATE = 5;
    private static final int BOOK_VOTE_RESULT = 6;
    private static final int BOOK_VOTE_COUNT = 7;
    private static final int BOOK_DESCRIPTION = 8;
    protected String title;
    protected String authorName;
    protected String uri;
    protected String description;
    protected String form;
    protected long size;
    protected long updateDate;//read from samlib
    protected long modifyTime;//change in BD
    protected boolean isNew;
    protected int id;
    protected int group_id;
    private long authorId;

    /**
     * Default constructor
     */
    public Book() {
        isNew = false;
        updateDate = Calendar.getInstance().getTime().getTime();
        modifyTime = Calendar.getInstance().getTime().getTime();
    }

    /**
     * Parsing HTTP get string and construct Book object
     *
     * @param string2parse input single string to parse
     */
    public Book(String string2parse) throws BookParseException {
        this();
        String[] strs = string2parse.split(SPLIT);
        title = strs[BOOK_TITLE];
        authorName = strs[BOOK_AUTHOR];
        uri = strs[BOOK_LINK];
        description = strs[BOOK_DESCRIPTION];
        form = strs[BOOK_FORM];
        try {
            size = Long.valueOf(strs[BOOK_SIZE]);
        } catch (NumberFormatException ex) {
            size = 0;
            //System.out.println("NumberFormatException!");
            //System.out.println("- "+string2parse);
        }
        Calendar cal = string2Cal(strs[BOOK_DATE]);


        updateDate = cal.getTimeInMillis();


    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.uri != null ? this.uri.hashCode() : 0);
        hash = 13 * hash + (int) (this.size ^ (this.size >>> 32));
        hash = 13 * hash + (int) (this.updateDate ^ (this.updateDate >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Book other = (Book) obj;
        if ((this.uri == null) ? (other.uri != null) : !this.uri.equals(other.uri)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        if (this.updateDate != other.updateDate) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        Date d = new Date(updateDate);
        return "Book{" + "uri=" + uri + ", size=" + size + ", updateDate=" + d + '}';
    }

    public static int testSplit(String str) {
        String[] arr = str.split(SPLIT);
        return arr.length;
    }

    /**
     * Get book url to open it using web browser
     * @return 
     */
    public String getUrlForBrowser(){
        return SamLibConfig.getBookUrlForBrowser(this);
    }
    
    /**
     * Get file object to store book for offline reading
     * @return 
     */
    public File getFile() {
        return DataExportImport._getBookFile(this);
    }

    /**
     * Get URL to open book for offline reading
     * @return 
     */
    public String getFileURL() {
        return "file://" + getFile().getAbsolutePath();
    }

    public void cleanFile() {
        File ff = DataExportImport._getBookFile(this);

        if (ff.exists()) {
            ff.delete();
        }
    }

    /**
     * Test whether file for the book is fresh enafght
     *
     *
     * @return true if we need update file
     */
    public boolean needUpdateFile() {

        File ff = getFile();

        if (!ff.exists()) {
            return true;
        }
        if (ff.lastModified() < getModifyTime()) {
            return true;
        }
        return false;
    }

    public static Calendar string2Cal(String str) throws BookParseException {
        String[] dd = str.split("/");

        if (dd.length != 3) {
            throw new BookParseException("Date string: " + str);
        }
        int day = Integer.valueOf(dd[0]);
        int month = Integer.valueOf(dd[1]);
        int year = Integer.valueOf(dd[2]);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.YEAR, year);


        return cal;
    }
}
