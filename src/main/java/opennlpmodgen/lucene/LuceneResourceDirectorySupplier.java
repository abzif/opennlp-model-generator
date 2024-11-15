package opennlpmodgen.lucene;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import opennlpmodgen.tools.util.ResourceDirectorySupplier;
import opennlpmodgen.tools.util.RootDirectorySupplier;

import java.nio.file.Path;

@RequiredArgsConstructor
public class LuceneResourceDirectorySupplier implements ResourceDirectorySupplier {
    @NonNull
    private final RootDirectorySupplier rootDirectorySupplier;

    @Override
    public Path getResourceDirectory(String language) {
        return rootDirectorySupplier.getRootDirectory().resolve(String.format("%s-lucene", language));
    }
}
