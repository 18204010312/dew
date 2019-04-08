/*
 * Copyright 2019. the original author or authors.
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

package ms.dew.devops.it;

import io.kubernetes.client.ApiException;
import org.junit.Before;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author gudaoxuri
 */
public class BasicProcessor {

    protected String kubeConfig;
    protected String dockerHost;
    protected String dockerRegistryUrl;
    protected String dockerRegistryUserName;
    protected String dockerRegistryPassword;
    protected String itSnapshotRepositoryId;
    protected String itSnapshotRepositoryUrl;

    @Before
    public void loadConfig() throws IOException, ApiException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(Paths.get("").toFile().getAbsolutePath() + File.separator + "devops.properties"));
        kubeConfig = properties.getProperty("dew.devops.kube.config");
        dockerHost = properties.getProperty("dew.devops.docker.host");
        dockerRegistryUrl = properties.getProperty("dew.devops.docker.registry.url");
        dockerRegistryUserName = properties.getProperty("dew.devops.docker.registry.username");
        dockerRegistryPassword = properties.getProperty("dew.devops.docker.registry.password");
        itSnapshotRepositoryId = properties.getProperty("dew.devops.it.snapshotRepository.id");
        itSnapshotRepositoryUrl = properties.getProperty("dew.devops.it.snapshotRepository.url");
    }
}