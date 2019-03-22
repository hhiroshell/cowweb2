/*
 * Copyright (c) 2019 Hiroshi Hayakawa <hhiroshell@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.gr.java_conf.hhiroshell.cowweb.model;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;

/**
 * DataSource
 *
 * TODO: DBダウン時でもDataSourceがSingletonがそのまま使われる問題
 */
public class CowwebDataSource {

    private static final String KEY_DATABASE_USERNAME = "DB_USER";

    private static final String KEY_DATABASE_PASSWORD = "DB_PASSWORD";

    private static final String KEY_DATABASE_URL = "DB_URL";

    private static final String KEY_DATABASE_PORT = "DB_PORT";

    private static DataSource instance;

    // uninstanciable
    private CowwebDataSource() {}

    static public DataSource getInstance() {
        if (instance == null) {
            instance = getDataSource();
        }
        return instance;
    }

    /**
     * データソースを取得する
     *
     * @return データソース
     */
    private static DataSource getDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(new StringBuilder()
                .append("jdbc:mysql://")
                .append(System.getenv(KEY_DATABASE_URL))
                .append(":")
                .append(System.getenv(KEY_DATABASE_PORT))
                .append("/COWWEB")
                .toString());
        dataSource.setUser(System.getenv(KEY_DATABASE_USERNAME));
        dataSource.setPassword(System.getenv(KEY_DATABASE_PASSWORD));
        return dataSource;
    }

}
