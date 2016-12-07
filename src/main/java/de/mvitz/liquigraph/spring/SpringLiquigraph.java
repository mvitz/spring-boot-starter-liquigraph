/*
 * Copyright 2016 the original author or authors.
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
package de.mvitz.liquigraph.spring;

import org.liquigraph.core.api.Liquigraph;
import org.liquigraph.core.configuration.Configuration;
import org.liquigraph.core.configuration.ConfigurationBuilder;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;

/**
 * A Spring-ified wrapper for {@link Liquigraph}.
 *
 * @author Michael Vitz
 */
public final class SpringLiquigraph implements InitializingBean {

    private final DataSource dataSource;
    private final String changeLog;

    public SpringLiquigraph(DataSource dataSource, String changeLog) {
        this.dataSource = dataSource;
        this.changeLog = changeLog;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final Configuration configuration = new ConfigurationBuilder()
            .withMasterChangelogLocation(changeLog)
            .withDataSource(dataSource)
            .withRunMode()
            .build();
        new Liquigraph().runMigrations(configuration);
    }
}
