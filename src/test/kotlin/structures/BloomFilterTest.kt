package structures

import com.google.common.hash.BloomFilter
import com.google.common.hash.Funnels
import net.andreinc.mockneat.unit.user.Emails
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.Charset
import structures.BloomFilter as KBloomFilter

@Suppress("UnstableApiUsage")
internal class BloomFilterTest {

    companion object {
        private const val N = 1000
        private const val FPP = 0.01
        private const val EPS = 0.01
    }

    @Test
    fun precisionTest() {
        val guavaBloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), N, FPP)
        repeat(N) { guavaBloomFilter.put(genEmail()) }
        checkFpp { guavaBloomFilter.mightContain(it) }

        val bloomFilter = KBloomFilter<String>(N, FPP)
        repeat(N) { bloomFilter.put(genEmail()) }
        checkFpp { bloomFilter[it] }
    }

    private fun checkFpp(filter: (String) -> Boolean) =
        (0..N * 10_000).map { genEmail() }
            .count(filter)
            .let {
                val fpp = it.toDouble() / (N * 10_000)
                println("False-positive probability: $fpp")
                assertTrue(fpp < FPP + EPS)
            }

    /**
     * Generating a random email address.
     * It is assumed that the probability of duplicate results is extremely low.
     */
    private fun genEmail(): String = Emails.emails().domain("gmail.com").`val`()
}