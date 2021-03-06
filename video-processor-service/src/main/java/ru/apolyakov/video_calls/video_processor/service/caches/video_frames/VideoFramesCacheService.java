package ru.apolyakov.video_calls.video_processor.service.caches.video_frames;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import ru.apolyakov.video_calls.video_processor.Constants;
import ru.apolyakov.video_calls.video_processor.data.VideoFrame;
import ru.apolyakov.video_calls.video_processor.service.caches.AbstractCacheService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class VideoFramesCacheService extends AbstractCacheService<Pair<String, Long>, PriorityQueue<VideoFrame>> implements Service {
    @Override
    public void cancel(ServiceContext serviceContext) {

    }

    @Override
    public void init(ServiceContext serviceContext) throws Exception {
        getOrCreateCache();
    }

    @Override
    public void execute(ServiceContext serviceContext) throws Exception {

    }

    @Override
    protected String getCacheName() {
        return Constants.Caches.VIDEO_FRAMES_CACHE;
    }

    public List<Pair<String, Long>> getKeysForCallSession(String callSid) {
        List<Pair<String, Long>> keys = new ArrayList<>();
        cache.query(new ScanQuery<Pair<String, Long>, PriorityQueue<VideoFrame>>(null)).forEach(entry -> {
            Pair<String, Long> key = entry.getKey();
            if (key.getLeft().equals(callSid)) {
                keys.add(key);
            }
        });
        keys.forEach(this::delete);
        return keys;
    }

    public Map<Integer, List<Pair<String, Long>>> partIdToKeysMap(Collection<Pair<String, Long>> keys) {
        return keys.stream().collect(Collectors.groupingBy(this::getPartition));
    }

    public void removeFramesForUnactiveSessions(Set<String> sids) {

    }

    @Override
    public Pair<String, Long> put(Pair<String, Long> key, PriorityQueue<VideoFrame> value) {
        cache.put(key, value);
        return key;
    }

    @Override
    public void delete(Pair<String, Long> key) {
        cache.remove(key);
    }
}
