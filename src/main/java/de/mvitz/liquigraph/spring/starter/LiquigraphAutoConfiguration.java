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
package de.mvitz.liquigraph.spring.starter;

import de.mvitz.liquigraph.spring.SpringChangelogLoader;
import de.mvitz.liquigraph.spring.SpringLiquigraph;

import org.liquigraph.core.api.Liquigraph;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

import javax.sql.DataSource;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Liquigraph.
 *
 * @author Michael Vitz
 */
@Configuration
@ConditionalOnClass(Liquigraph.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(prefix = "liquigraph", name = "enabled", matchIfMissing = true)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class LiquigraphAutoConfiguration {

    @Configuration
    @ConditionalOnMissingBean(SpringLiquigraph.class)
    @EnableConfigurationProperties(LiquigraphProperties.class)
    public static class LiquigraphConfiguration {

        private final LiquigraphProperties properties;
        private final DataSource dataSource;
        private final DataSource liquigraphDataSource;

        public LiquigraphConfiguration(LiquigraphProperties properties,
                                       DataSource dataSource,
                                       @LiquigraphDataSource ObjectProvider<DataSource> liquigraphDataSourceProvider) {
            this.properties = properties;
            this.dataSource = dataSource;
            this.liquigraphDataSource = liquigraphDataSourceProvider.getIfAvailable();
        }

        @Bean
        public SpringLiquigraph liquigraph(ResourceLoader loader) throws IOException {
            final SpringChangelogLoader changelogLoader = new SpringChangelogLoader(loader);
            return new SpringLiquigraph(getDataSource(), changelogLoader, properties.getChangeLog());
        }

        private DataSource getDataSource() {
            if (liquigraphDataSource != null) {
                return liquigraphDataSource;
            } else if (properties.getUrl() == null) {
                return dataSource;
            } else {
                return DataSourceBuilder
                    .create()
                        .url(properties.getUrl())
                        .username(properties.getUser())
                        .password(properties.getPassword())
                    .build();
            }
        }
    }
}
